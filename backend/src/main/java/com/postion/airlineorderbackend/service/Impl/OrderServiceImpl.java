package com.postion.airlineorderbackend.service.Impl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.CreateOrderRequest;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.dto.UpdateOrderRequest;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.enums.OrderStatus;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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
        try {
            order.setStatus(OrderStatus.valueOf(request.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid order status: " + request.getStatus());
        }
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
        	try {
        		existing.setStatus(OrderStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid order status: " + request.getStatus());
            }
            existing.setAmount(request.getAmount());
            existing.setCreationDate(request.getCreationDate());

            if (request.getUserId() != null) {
                userRepository.findById(request.getUserId()).ifPresent(existing::setUser);
            }

            Order updated = orderRepository.save(existing);
            return OrderDTO.fromEntity(updated);
        });
    }

    @Override
    public void cancelUnpaidOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(15);
        List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDateBefore(OrderStatus.PENDING_PAYMENT, deadline);

        if(!unpaidOrders.isEmpty()) {
        	for (Order order : unpaidOrders) {
                order.setStatus(OrderStatus.CANCELLED);
            }
        }else {
        	log.info("未发现支付超时的订单。");
        }
        

        orderRepository.saveAll(unpaidOrders);
    }

}
