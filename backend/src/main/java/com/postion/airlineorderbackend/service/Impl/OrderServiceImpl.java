package com.postion.airlineorderbackend.service.Impl;

import com.postion.airlineorderbackend.client.AirlineApiClient;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AirlineApiClient airlineApiClient;

    /**
     * 验证订单状态转换的合法性
     *
     * @param currentStatus 当前状态
     * @param newStatus     目标状态
     * @throws BusinessException 如果状态转换不合法
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // 定义合法的状态转换映射
        Map<OrderStatus, List<OrderStatus>> validTransitions = new HashMap<>();
        validTransitions.put(OrderStatus.PENDING_PAYMENT, List.of(OrderStatus.PAID, OrderStatus.CANCELLED));
        validTransitions.put(OrderStatus.PAID, List.of(OrderStatus.TICKETING_IN_PROGRESS, OrderStatus.CANCELLED));
        validTransitions.put(OrderStatus.TICKETING_IN_PROGRESS,
                List.of(OrderStatus.TICKETED, OrderStatus.TICKETING_FAILED));
        validTransitions.put(OrderStatus.TICKETING_FAILED,
                List.of(OrderStatus.TICKETING_IN_PROGRESS, OrderStatus.CANCELLED));
        validTransitions.put(OrderStatus.TICKETED, List.of()); // 已出票状态不允许转换
        validTransitions.put(OrderStatus.CANCELLED, List.of()); // 已取消状态不允许转换

        // 检查转换是否合法
        if (!validTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    String.format("不允许从 %s 状态转换到 %s 状态", currentStatus, newStatus));
        }
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在"));

        OrderDto orderDto = orderMapper.toDto(order);
        orderDto.setFlightInfo(getMockFlightInfo(order.getOrderNumber()));
        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在"));

        // 验证状态转换合法性
        validateStatusTransition(order.getStatus(), OrderStatus.PAID);

        order.setStatus(OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    @Override
    @Async
    @Transactional
    public void requestTicketIssuance(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在"));

        // 验证状态转换合法性
        log.info("开始为订单 {} 调用航司接口出票...当前状态: {}", id, order.getStatus());

        try {
            validateStatusTransition(order.getStatus(), OrderStatus.TICKETING_IN_PROGRESS);

            order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
            orderRepository.save(order);

            // 调用航司API出票
            airlineApiClient.issueTicket(id);

            // 出票成功
            validateStatusTransition(order.getStatus(), OrderStatus.TICKETED);
            order.setStatus(OrderStatus.TICKETED);
            orderRepository.save(order);
            log.info("订单 {} 出票成功", id);
        } catch (Exception e) {
            validateStatusTransition(order.getStatus(), OrderStatus.TICKETING_FAILED);
            order.setStatus(OrderStatus.TICKETING_FAILED);
            orderRepository.save(order);
            log.error("订单 {} 出票失败", id, e);
        }
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在"));

        // 验证状态转换合法性
        validateStatusTransition(order.getStatus(), OrderStatus.CANCELLED);

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    // 生成模拟航班信息
    private Map<String, Object> getMockFlightInfo(String orderNumber) {
        Map<String, Object> flightInfo = new HashMap<>();
        flightInfo.put("flightNumber", "MU" + (1000 + orderNumber.hashCode() % 9000));
        flightInfo.put("departureCity", "Beijing");
        flightInfo.put("arrivalCity", "Shanghai");
        flightInfo.put("departureTime", LocalDateTime.now().plusDays(1).toString());
        flightInfo.put("arrivalTime", LocalDateTime.now().plusDays(1).plusHours(2).toString());
        return flightInfo;
    }
}