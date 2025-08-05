package com.postion.airlineorderbackend.service;

import java.util.List;

import com.postion.airlineorderbackend.dto.OrderDto;

public interface OrderService {
    OrderDto getOrderById(Long id);

    List<OrderDto> getAllOrders();

    OrderDto payOrder(Long id);

    OrderDto cancelOrder(Long id);

    void cancelUnpaidOrders();

    void requestTicketIssuance(Long id);
}
