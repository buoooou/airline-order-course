package com.postion.airlineorderbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.statemachine.OrderStatus;
import com.postion.airlineorderbackend.model.FlightInfo;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FlightApiService flightApiService;

    public List<Order> listOrders(Long userid) {
        return orderRepository.findByUserId(userid);
    }

    public Optional<Order> getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        order.ifPresent(o -> {
            FlightInfo flightInfo = flightApiService.getFlightInfo(o.getOrderNumber());
            o.setFlightInfo(flightInfo);
        });
        return order;
    }

    public Order createOrder(Order order) {
    	order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        return orderRepository.save(order);
    }

    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        OrderStatus currentStatus = OrderStatus.valueOf(existingOrder.getStatus());
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
        existingOrder.setStatus(newStatus.name());
        return orderRepository.save(existingOrder);
    }

}