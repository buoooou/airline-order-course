package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.adapter.outbound.AirLineApiClient;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j // 添加日志注解
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AirLineApiClient airLineApiClient;
    private final OrderMapper orderMapper;

    @Autowired // 建议添加Autowired注解明确依赖注入
    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, AirLineApiClient airLineApiClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.airLineApiClient = airLineApiClient;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> ordersList = orderRepository.findAll();
        // 修正：使用orderMapper的toDto方法
        return ordersList.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        // 修正：findById返回Optional，需要处理空值情况
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在: " + id));
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto payOrder(Long id) {
        log.info("开始处理支付订单请求, 订单ID: {}", id);
        Order order = findOrderById(id);

        // 状态机校验: 只有 PENDING_PAYMENT 状态的订单才能支付
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            log.warn("支付失败: 订单 {} 状态不是 PENDING_PAYMENT, 当前状态为 {}", id, order.getStatus());
            throw new BusinessException(HttpStatus.BAD_REQUEST, "只有待支付的订单才能支付。当前状态: " + order.getStatus());
        }

        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);
        log.info("订单 {} 状态已更新为 PAID", id);

        // 异步触发下一步: 出票
        requestTicketIssuance(order.getId());
        return orderMapper.toDto(savedOrder);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在: " + id));
    }

    @Override
    public void requestTicketIssuance(Long id) {
        // 可添加出票逻辑，例如调用airLineApiClient
    }

    @Override
    public OrderDto cancelOrder(Long id) {
        // 示例实现：取消订单逻辑
        Order order = findOrderById(id);
        // 状态校验：只有未支付或已支付但未出票的订单可取消（根据业务调整）
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "订单已取消");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        return orderMapper.toDto(cancelledOrder);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    @SchedulerLock(
            name = "cancelUnpaidOrdersTask",
            lockAtMostFor = "55s",
            lockAtLeastFor = "10s"
    )
    public void cancelUnpaidOrders() {
        log.info("【定时任务】开始检查并取消支付超时的订单...");
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDateBefore(
                OrderStatus.PENDING_PAYMENT,
                fifteenMinutesAgo
        );

        if (!unpaidOrders.isEmpty()) {
            log.info("【定时任务】发现 {} 个超时订单, 将它们的状态更新为 CANCELLED", unpaidOrders.size());
            for (Order order : unpaidOrders) {
                order.setStatus(OrderStatus.CANCELLED);
                log.debug(" - 订单 {} (创建于 {}) 已被标记为取消", order.getId(), order.getCreationDate());
            }
            orderRepository.saveAll(unpaidOrders);
        } else {
            log.info("【定时任务】未发现支付超时的订单。");
        }
    }

}