package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderRequest;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public Order createOrder(OrderRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(Order.OrderStatus.PENDING_PAYMENT);
        order.setAmount(request.getAmount());
        order.setCreationDate(LocalDateTime.now());
        order.setUser(user);

        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserId(user.getId());
    }

    public Order getOrderById(Long orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        return order;
    }

    public Order payOrder(Long orderId, String username) {
        Order order = getOrderById(orderId, username);
        
        if (order.getStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Order cannot be paid in current status");
        }
        
        order.transitionTo(Order.OrderStatus.PAID);
        order = orderRepository.save(order);
        
        // 异步处理出票
        processTicketing(order.getId());
        
        return order;
    }

    public Order cancelOrder(Long orderId, String username) {
        Order order = getOrderById(orderId, username);
        
        if (order.getStatus() == Order.OrderStatus.TICKETED) {
            throw new RuntimeException("Cannot cancel ticketed order");
        }
        
        order.transitionTo(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Order retryTicketing(Long orderId, String username) {
        Order order = getOrderById(orderId, username);
        
        if (order.getStatus() != Order.OrderStatus.TICKETING_FAILED) {
            throw new RuntimeException("Order is not in ticketing failed status");
        }
        
        order.transitionTo(Order.OrderStatus.TICKETING_IN_PROGRESS);
        order = orderRepository.save(order);
        
        // 异步处理出票
        processTicketing(order.getId());
        
        return order;
    }

    @Async
    public void processTicketing(Long orderId) {
        try {
            // 模拟出票处理时间
            Thread.sleep(5000);
            
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            // 模拟出票成功率90%
            if (Math.random() < 0.9) {
                order.transitionTo(Order.OrderStatus.TICKETED);
            } else {
                order.transitionTo(Order.OrderStatus.TICKETING_FAILED);
            }
            
            orderRepository.save(order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public void cancelExpiredOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);
        List<Order> expiredOrders = orderRepository.findPendingPaymentOrdersBefore(cutoffTime);
        
        for (Order order : expiredOrders) {
            order.transitionTo(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 