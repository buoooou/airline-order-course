package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDto;
import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrders();
    OrderDto getOrderById(Long id);
}