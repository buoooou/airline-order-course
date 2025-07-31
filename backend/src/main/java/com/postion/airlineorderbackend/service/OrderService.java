package com.postion.airlineorderbackend.service;

import java.util.List;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;

public interface  OrderService {
    List<OrderDto> getAllOrders();
    OrderDto getOrderById(Long id);
    OrderDto payOrder(Long id);
    void retryTicketingIssuance(Long id);
    OrderDto cancelOrder(Long id);

}
