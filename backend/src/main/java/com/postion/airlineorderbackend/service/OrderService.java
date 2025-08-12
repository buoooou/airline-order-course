package com.postion.airlineorderbackend.service;

import java.util.List;
import java.util.Optional;

import com.postion.airlineorderbackend.dto.CreateOrderRequest;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.dto.UpdateOrderRequest;

public interface OrderService {
    List<OrderDTO> getAllOrders();
    Optional<OrderDTO> getOrderById(Long id);
    OrderDTO createOrder(CreateOrderRequest request);
    Optional<OrderDTO> updateOrder(Long id, UpdateOrderRequest request);
    void cancelUnpaidOrders();

}
