package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;
import com.postion.airlineorderbackend.statemachine.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto createOrder(Long userId, Long flightId, BigDecimal amount) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setFlightId(flightId);
        order.setAmount(amount);
        order.setStatus(OrderState.PENDING_PAYMENT.name());
        order.setCreationDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("创建订单成功: 订单号={}, 用户ID={}, 航班ID={}", savedOrder.getOrderNumber(), userId, flightId);

        return orderMapper.toDto(savedOrder);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
    }

    @Override
    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
    }

}