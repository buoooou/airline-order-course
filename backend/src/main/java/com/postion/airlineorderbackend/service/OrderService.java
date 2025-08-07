package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.OrderStatus;

import java.util.List;

/**
 * interface for order management services.
 * Defines the contract for business logic related to flight orders.
 */
public interface OrderService {

    List<OrderDto> getAllOrders();
    OrderDto getOrderById(Long id);
    OrderDto payOrder(Long id);
    void requestTicketIssuance(Long id);
    OrderDto cancelOrder(Long id);
    OrderDto updateStatus(Long id, OrderStatus newStatus);
}
