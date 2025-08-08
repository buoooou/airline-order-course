package com.position.airline_order_course.service;

import java.util.List;

import com.position.airline_order_course.dto.OrderDto;

/*
 * 订单服务接口
 */
public interface OrderService {

    List<OrderDto> getAllOrders();

    OrderDto getOrderById(Long id);
}
