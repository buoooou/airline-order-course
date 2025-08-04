package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.OrderStatus;

import java.util.List;

/**
 * interface for order management services.
 * Defines the contract for business logic related to flight orders.
 */
public interface OrderService {
    /**
     * Retrieves a list of all orders, converted to DTOs.
     * @return A list of {@link OrderDto} objects.
     */
    List<OrderDto> getAllOrders();

    /**
     * Retrieves a single order by its unique identifier and enriches it with additional information.
     * @param id The unique ID of the order to retrieve.
     * @return The corresponding {@link OrderDto}, enriched with flight information.
     * @throws RuntimeException If an order with the given ID is not found.
     */
    OrderDto getOrderById(Long id);

    OrderDto payOrder(Long id);

    /**
     * 这是一个异步触发方法
     */
    void requestTicketIssuance(Long id);

    OrderDto cancelOrder(Long id);

    OrderDto updateStatus(Long id, OrderStatus newStatus);
}
