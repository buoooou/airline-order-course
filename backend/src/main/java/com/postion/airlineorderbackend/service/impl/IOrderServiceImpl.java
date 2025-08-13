package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.converter.Converter;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.IOrderService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class IOrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;

    /**
     * 获取所有订单的列表，转换为OrderDto对象。
     *
     * @return 所有订单的列表
     */
    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orderList = orderRepository.findAll();
        return Converter.INSTANCE.toResOrderDtoList(orderList);
    }

    /**
     * 根据用户ID获取单个订单，并附加航班信息。
     *
     * @param userId 用户ID
     * @return 对应的OrderDto对象，包含航班信息
     */
    @Override
    public List<OrderDto> getAllOrdersByUserId(Long userId) {
        List<Order> orderList = orderRepository.findAllByUserId(userId);
        return Converter.INSTANCE.toResOrderDtoList(orderList);
    }

    /**
     * 根据唯一标识符获取单个订单，并附加航班信息。
     *
     * @param id 订单的唯一ID
     * @return 对应的OrderDto对象，包含航班信息
     * @throws RuntimeException 如果找不到指定ID的订单
     */
    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        if(order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Order already paid yet.");
        }
        return Converter.INSTANCE.toResOrderDto(order);

    }

    /**
     * 支付订单。
     *
     * @param id 订单的唯一ID
     * @return 支付后的OrderDto对象
     */
    @Transactional
    @Override
    public OrderDto payOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        if(order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Order already paid yet.");
        }

        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);

        requestTicketIssuance(order.getId());
        return Converter.INSTANCE.toResOrderDto(savedOrder);
    }


    /**
     * 异步触发方法，请求出票。
     *
     * @param id 订单的唯一ID
     */
    @Override
    public void requestTicketIssuance(Long id) {

    }

    /**
     * 取消订单。
     *
     * @param id 订单的唯一ID
     * @return 取消后的OrderDto对象
     */
    @Override
    public OrderDto cancelOrder(Long id) {
        return null;
    }

    /**
     * 批量取消超时订单
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    @SchedulerLock(name = "cancelUnpaidOrdersTask", lockAtLeastFor = "10s", lockAtMostFor = "55s")
    @Override
    public void cancelUnpaidOrder() {
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Order> unpaidOrder = orderRepository.findByStatusAndCreationDateBefore(OrderStatus.PENDING_PAYMENT, fifteenMinutesAgo);

        if(!unpaidOrder.isEmpty()) {
            for (Order order : unpaidOrder) {
                order.setStatus(OrderStatus.CANCELLED);
            }
            orderRepository.saveAll(unpaidOrder);
        }
    }
}
