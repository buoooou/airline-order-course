package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    
    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        return convertToDto(order);
    }
    
    @Override
    public OrderDto payOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        order.setStatus(OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }
    
    @Override
    public OrderDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }
    
    @Override
    public OrderDto updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }
    
    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        dto.setCreationDate(order.getCreationDate());
        
        if (order.getUser() != null) {
            OrderDto.UserDto userDto = new OrderDto.UserDto();
            userDto.setId(order.getUser().getId());
            userDto.setUsername(order.getUser().getUsername());
            dto.setUser(userDto);
        }
        
        return dto;
    }
}
