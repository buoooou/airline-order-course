package com.postion.airlineorderbackend.service.impl;


import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = toEntity(orderDto);
        order.setId(null); // 确保是新建
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        // 只更新允许修改的字段
        order.setOrderNumber(orderDto.getOrderNumber());
        order.setAmount(orderDto.getAmount());
        order.setStatus(orderDto.getStatus());
        // 用户更新（如有需要）
        if (orderDto.getUser() != null && orderDto.getUser().getId() != null) {
            Optional<User> userOpt = userRepository.findById(orderDto.getUser().getId());
            userOpt.ifPresent(order::setUser);
        }
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public OrderDto payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order is not pending payment");
        }
        order.setStatus(OrderStatus.PAID);
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void requestTicketIssuance(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        // 这里可以实现出票逻辑
        order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    // DTO <-> Entity 转换方法
    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        dto.setCreationDate(order.getCreationDate());
        if (order.getUser() != null) {
            UserDto userDto = new UserDto();
            userDto.setId(order.getUser().getId());
            userDto.setUsername(order.getUser().getUsername());
            dto.setUser(userDto);
        }
        // flightInfo 可根据实际业务补充
        return dto;
    }

    private Order toEntity(OrderDto dto) {
        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderNumber(dto.getOrderNumber());
        order.setStatus(dto.getStatus());
        order.setAmount(dto.getAmount());
        order.setCreationDate(dto.getCreationDate());
        if (dto.getUser() != null && dto.getUser().getId() != null) {
            userRepository.findById(dto.getUser().getId()).ifPresent(order::setUser);
        }
        // flightInfo 可根据实际业务补充
        return order;
    }
}
