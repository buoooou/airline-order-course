package com.airline.service;

import com.airline.dto.OrderCreateDto;
import com.airline.dto.OrderDto;
import com.airline.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderDto createOrder(OrderCreateDto createDto, Long userId);

    Optional<OrderDto> getOrderById(Long id);

    Optional<OrderDto> getOrderByNumber(String orderNumber);

    Page<OrderDto> getAllOrders(Pageable pageable);

    Page<OrderDto> getOrdersByUser(Long userId, Pageable pageable);

    Page<OrderDto> getOrdersByStatus(Order.Status status, Pageable pageable);

    Page<OrderDto> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus, Pageable pageable);

    Page<OrderDto> searchOrders(String keyword, Pageable pageable);

    OrderDto updateOrder(Long id, OrderDto orderDto);

    OrderDto updateOrderStatus(Long id, Order.Status status);

    OrderDto updatePaymentStatus(Long id, Order.PaymentStatus paymentStatus);

    void cancelOrder(Long id);

    OrderDto processPayment(Long id, Order.PaymentMethod paymentMethod);

    List<OrderDto> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    void cleanupExpiredOrders();

    long countOrdersByStatus(Order.Status status);

    long countOrdersByPaymentStatus(Order.PaymentStatus paymentStatus);

    String generateOrderNumber();
}