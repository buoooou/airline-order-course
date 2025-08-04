package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDto;
import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrders();

    OrderDto getOrderById(Long id);

    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     */
    void payOrder(Long orderId);

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     */
    void cancelOrder(Long orderId);
}