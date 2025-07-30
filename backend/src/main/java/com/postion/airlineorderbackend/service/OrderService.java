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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

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
        order.setFlightNumber(request.getFlightNumber());
        order.setDepartureCity(request.getDepartureCity());
        order.setArrivalCity(request.getArrivalCity());
        order.setDepartureTime(request.getDepartureTime());
        order.setArrivalTime(request.getArrivalTime());

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
        
        logger.info("[payOrder] 订单ID: {}, 当前状态: {}", orderId, order.getStatus());
        
        if (order.getStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            logger.error("[payOrder] 订单状态错误: 期望 PENDING_PAYMENT, 实际 {}", order.getStatus());
            throw new RuntimeException("Order cannot be paid in current status: " + order.getStatus());
        }
        
        logger.info("[payOrder] 开始状态转换: PENDING_PAYMENT -> PAID");
        order.transitionTo(Order.OrderStatus.PAID);
        order = orderRepository.save(order);
        logger.info("[payOrder] 状态转换完成，订单ID: {}, 新状态: {}", orderId, order.getStatus());
        
        // 暂时禁用异步出票处理，避免状态冲突
        // logger.info("[payOrder] 启动异步出票处理，订单ID: {}", orderId);
        // processTicketing(order.getId());
        
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
            logger.info("[processTicketing] 开始处理出票，订单ID: {}", orderId);
            
            // 模拟出票处理时间
            Thread.sleep(5000);
            
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            logger.info("[processTicketing] 订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            
            // 模拟出票成功率90%
            if (Math.random() < 0.9) {
                logger.info("[processTicketing] 出票成功，订单ID: {}, 状态转换: {} -> TICKETED", orderId, order.getStatus());
                order.transitionTo(Order.OrderStatus.TICKETED);
            } else {
                logger.info("[processTicketing] 出票失败，订单ID: {}, 状态转换: {} -> TICKETING_FAILED", orderId, order.getStatus());
                order.transitionTo(Order.OrderStatus.TICKETING_FAILED);
            }
            
            orderRepository.save(order);
            logger.info("[processTicketing] 出票处理完成，订单ID: {}, 最终状态: {}", orderId, order.getStatus());
        } catch (InterruptedException e) {
            logger.error("[processTicketing] 出票处理被中断，订单ID: {}", orderId);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("[processTicketing] 出票处理异常，订单ID: {}, 错误: {}", orderId, e.getMessage());
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