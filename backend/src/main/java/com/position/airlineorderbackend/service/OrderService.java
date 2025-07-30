package com.position.airlineorderbackend.service;

import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.dto.OrderDto;
import java.util.List;

public interface OrderService {
    String payOrder(Long id);
    String startTicketing(Long id);
    String completeTicketing(Long id);
    String failTicketing(Long id);
    String cancelOrder(Long id);
    String retryPayment(Long id);
    String retryTicketing(Long id);
    List<OrderDto> getAllOrders();
    OrderDto getOrderById(Long id);
} 