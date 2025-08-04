package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.OrderDto.UserDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        OrderDto orderDto = convertToDto(order);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Order cannot be paid as it's not in PENDING status");
        }

        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    @Override
    public void requestTicketIssuance(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Ticket can only be issued for PAID orders");
        }

        System.out.println("Ticket issuance requested for order: " + id);
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.TICKETED) {
            throw new RuntimeException("Completed orders cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    @Transactional
    public OrderDto updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        OrderStatus currentStatus = order.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(String.format(
                    "Invalid status transition from %s to %s for order %d",
                    currentStatus, newStatus, id));
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING_PAYMENT:
                return newStatus == OrderStatus.PAID ||
                        newStatus == OrderStatus.CANCELLED;

            case PAID:
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS ||
                        newStatus == OrderStatus.CANCELLED;

            case TICKETING_IN_PROGRESS:
                return newStatus == OrderStatus.TICKETED ||
                        newStatus == OrderStatus.TICKETING_FAILED;

            case TICKETING_FAILED:
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS ||
                        newStatus == OrderStatus.CANCELLED;

            case TICKETED:
                // Once ticketed, only cancellation is allowed
                return newStatus == OrderStatus.CANCELLED;

            case CANCELLED:
                // Cancelled orders cannot change status
                return false;

            default:
                throw new IllegalArgumentException("Unknown order status: " + currentStatus);
        }
    }

    private OrderDto convertToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setOrderNumber(order.getOrderNumber());
        orderDto.setStatus(order.getStatus());
        orderDto.setAmount(order.getAmount());
        orderDto.setCreationDate(order.getCreationDate());

        UserDto userDto = new UserDto();
        userDto.setId(order.getUser().getId()); // Assuming Order has getUserId()
        userDto.setUsername(order.getUser().getUsername()); // You would typically get this from user service
        orderDto.setUser(userDto);

        // Flight info would be populated from another service call
        // orderDto.setFlightInfo(flightService.getFlightInfo(order.getFlightId()));

        return orderDto;
    }
}