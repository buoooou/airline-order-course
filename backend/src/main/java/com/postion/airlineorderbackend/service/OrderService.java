package com.postion.airlineorderbackend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.entity.Flight;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.OrderStatus;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.repository.FlightRepository;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final FlightRepository flightRepo;

    public Order createOrder(String email, Long flightId) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Flight flight = flightRepo.findById(flightId).orElseThrow(() -> new RuntimeException("Flight not found"));

        return orderRepo.save(Order.builder()
                .user(user)
                .flight(flight)
                .status(OrderStatus.PENDING_PAYMENT)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new IllegalStateException("非法状态流转");
        }
        order.setStatus(newStatus);
        return orderRepo.save(order);

    }

    public boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case PENDING_PAYMENT:
                return next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID:
                return next == OrderStatus.TICKETING_IN_PROGRESS || next == OrderStatus.CANCELLED;
            case TICKETING_IN_PROGRESS:
                return next == OrderStatus.TICKETED || next == OrderStatus.TICKETING_FAILED || next == OrderStatus.CANCELLED;
            case TICKETING_FAILED:
                return next == OrderStatus.TICKETING_IN_PROGRESS || next == OrderStatus.CANCELLED;
            case TICKETED:
                return next == OrderStatus.CANCELLED;
            case CANCELLED:
                return false; // 已取消不能再变更
            default:
                return false;
        }
    }

    public OrderStatus updateStatus(OrderStatus current, OrderStatus next) {
        if (!isValidTransition(current, next)) {
            throw new IllegalStateException("Invalid state transition: " + current + " -> " + next);
        }
        return next;
    }
}
