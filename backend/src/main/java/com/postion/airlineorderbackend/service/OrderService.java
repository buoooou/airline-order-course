package com.postion.airlineorderbackend.service;

import java.util.List;

import com.postion.airlineorderbackend.dto.OrderDto;

public interface OrderService {

    /**. Retrieves a list of all orders, converted to DTOs. ... */
    List<OrderDto> getAllOrders();

    /** Retrieves a single order by its unique identifier and enriches it with additional information. ... */
    OrderDto getOrderById(Long id);

    OrderDto payOrder(Long id);

    void cancelOrder(Long id); //这是一个异步触发方法

    OrderDto retryTicketing(Long id);

}
