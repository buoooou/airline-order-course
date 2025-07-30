package com.postion.airlineorderbackend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.OrderDTO;

@Service
public interface OrderService {
    List<OrderDTO> getAllOrders(Long userid);

    OrderDTO getOrderByOrderNumber(String orderNumber);

    OrderDTO payOrder(Long id);

    OrderDTO cancelOrder(Long id);

    OrderDTO createOrder(OrderDTO orderDto);
}
