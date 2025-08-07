package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.constants.BusinessConstants;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.exception.ResourceNotFoundException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService, BusinessConstants {
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final AirlineApiClient airlineApiClient;
	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Override
	public List<OrderDto> getAllOrders() {
		List<Order> orderList = orderRepository.findAll();
		List<OrderDto> orderDtoList = new ArrayList<OrderDto>();
		for (Order order : orderList) {
			OrderDto orderDto = orderMapper.toDto(order);
			orderDtoList.add(orderDto);
		}
		return orderDtoList;
	}

	@Override
	public OrderDto getOrderById(Long id) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("未找到所选订单。订单号：" + id));
		OrderDto orderDto = orderMapper.toDto(order);
		return orderDto;
	}

	@Override
	@Transactional
	public OrderDto payOrder(Long id) {
		log.info("开始处理支付订单请求，订单ID: {}", id);

		Order savedOrder = updateStatus(id, OrderStatus.PAID);
		log.info("订单 {} 状态已更新为 PAID", id);

		// 异步触发下一步：出票
		requestTicketIssuance(id);
		return orderMapper.toDto(savedOrder);
	}

	@Override
	@Async // 这是一个异步触发方法
	public void requestTicketIssuance(Long id) {
		log.info("出票处理请求，订单ID: {}", id);

		updateStatus(id, OrderStatus.TICKETING_IN_PROGRESS);
		log.info("订单 {} 状态已更新为 TICKETING_IN_PROGRESS", id);

		try {
			String result = airlineApiClient.issueTicket(id);
			log.info("正常出票成功。出票号 {}", result);
			updateStatus(id, OrderStatus.TICKETED);
			log.info("订单 {} 状态已更新为 TICKETED", id);
		} catch (RuntimeException e) {
			log.warn("出票失败：失败原因 {}", id, e.getMessage());
			updateStatus(id, OrderStatus.TICKETING_FAILED);
			log.info("订单 {} 状态已更新为 TICKETING_FAILED", id);
		} catch (InterruptedException e) {
			log.warn("中断异常：失败原因 {}", id, e.getMessage());
			throw new BusinessException(HttpStatus.BAD_REQUEST, "航司系统出票中断。终端原因：" + e.getMessage());
		}
	}

	@Override
	public OrderDto cancelOrder(Long id) {
		log.info("开始处理取消订单请求，订单ID: {}", id);

		Order savedOrder = updateStatus(id, OrderStatus.CANCELLED);
		log.info("订单 {} 状态已更新为 CANCELLED", id);

		return orderMapper.toDto(savedOrder);
	}

	@Scheduled(fixedRate = 60000)
	@Transactional
	@SchedulerLock(name = "cancelUnpaidOrdersTask", lockAtMostFor = LOOK_AT_MOST_FOR, lockAtLeastFor = LOOK_AT_LAST_FOR)
	public void cancelUnpaidOrders() {
		log.info("【定时任务】开始检查并取消支付超时的订单...");
		LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
		List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDateBefore(OrderStatus.PENDING_PAYMENT,
				fifteenMinutesAgo);

		if (!unpaidOrders.isEmpty()) {
			log.info("【定时任务】发现 {} 个超时订单，将它们的状态更新为 CANCELLED", unpaidOrders.size());
			for (Order order : unpaidOrders) {
				order.setStatus(OrderStatus.CANCELLED);
				log.debug(" - 订单 {} (创建于 {}) 已被标记为取消", order.getId(), order.getCreationDate());
			}
			orderRepository.saveAll(unpaidOrders);
		} else {
			log.info("【定时任务】未发现支付超时的订单。");
		}
	}

	/**
	 * 订单状态转换共通
	 * 
	 * @param id        订单ID
	 * @param newStatus 变更后状态
	 * @return 订单信息
	 * @throws BusinessException    未找到所选订单
	 * @throws BusinessException    订单状态转换失败
	 */
	private Order updateStatus(Long id, OrderStatus newStatus) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("未找到所选订单。订单号：" + id));
		if (order.getStatus().canTransitionTo(newStatus)) {
			order.setStatus(newStatus);
			orderRepository.save(order);
		} else {
			log.warn("支付失败：订单 {} 状态转换失败，当前状态为 {}", id, order.getStatus());
			throw new BusinessException(HttpStatus.BAD_REQUEST,
					"当前状态无法转换为" + newStatus + "状态。当前状态：" + order.getStatus());
		}
		return order;
	}
}
