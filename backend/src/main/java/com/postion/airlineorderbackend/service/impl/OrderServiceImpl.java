package com.postion.airlineorderbackend.service.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.Exception.AirlineBusinessException;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public List<OrderDTO> getAllOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(x -> mapToDTO(x)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::mapToDTO)
                .orElseThrow(() -> new AirlineBusinessException("The order does not exist."));
    }

    @Transactional
    @Override
    public OrderDTO payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AirlineBusinessException("The order does not exist."));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return mapToDTO(order);
    }

    @Transactional
    @Override
    public OrderDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AirlineBusinessException("The order does not exist."));
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        return mapToDTO(order);
    }

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDto) {
        Order order = new Order();
        order.setOrderNumber("AA" + String.format("%06d", ThreadLocalRandom.current().nextInt(1000000)));
        order.setAmount(orderDto.getAmount());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        orderRepository.save(order);
        return mapToDTO(order);
    }

    private OrderDTO mapToDTO(Order entity) {
        OrderDTO orderDto = new OrderDTO();
        orderDto.setOrderNumber(entity.getOrderNumber());
        orderDto.setStatus(entity.getStatus());
        orderDto.setAmount(entity.getAmount());
        orderDto.setCreateTime(entity.getCreateTime());
        orderDto.setUpdateTime(entity.getUpdateTime());
        return orderDto;
    }
}
