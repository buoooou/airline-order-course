package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
    }
}