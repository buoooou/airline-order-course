package com.postion.airlineorderbackend.service.Impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.CreateOrderRequest;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.dto.UpdateOrderRequest;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.service.OrderService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderDTO::fromEntity);
    }
    
    @Override
    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderNumber(request.getOrderNumber());
        order.setStatus(request.getStatus());
        order.setAmount(request.getAmount());
        order.setCreationDate(request.getCreationDate());

        if (request.getUserId() != null) {
            userRepository.findById(request.getUserId()).ifPresent(order::setUser);
        }

        Order saved = orderRepository.save(order);
        return OrderDTO.fromEntity(saved);
    }

    @Override
    public Optional<OrderDTO> updateOrder(Long id, UpdateOrderRequest request) {
        return orderRepository.findById(id).map(existing -> {
            existing.setStatus(request.getStatus());
            existing.setAmount(request.getAmount());
            existing.setCreationDate(request.getCreationDate());

            if (request.getUserId() != null) {
                userRepository.findById(request.getUserId()).ifPresent(existing::setUser);
            }

            Order updated = orderRepository.save(existing);
            return OrderDTO.fromEntity(updated);
        });
    }


}
