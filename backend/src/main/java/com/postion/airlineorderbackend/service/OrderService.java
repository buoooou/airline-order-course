package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.entity.Order;
import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrders();

    List<OrderDto> getOrdersByUserId(Long userId);

    OrderDto getOrderById(Long id);

    /**
     * 创建订单
     * 
     * @param userId 用户ID
     * @param flightId 航班ID
     * @param amount 订单金额
     * @return 创建的订单DTO
     */
    OrderDto createOrder(Long userId, Long flightId, BigDecimal amount);

    /**
     * 根据ID查找订单实体
     * 
     * @param id 订单ID
     * @return 订单实体
     */
    Order findOrderById(Long id);


}