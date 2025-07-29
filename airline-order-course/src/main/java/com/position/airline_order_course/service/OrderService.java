package com.position.airline_order_course.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.position.airline_order_course.dto.OrderDto;
import com.position.airline_order_course.model.Order;
import com.position.airline_order_course.repo.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // 查询所有订单
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (Order order : orders) {
            OrderDto dto = new OrderDto();
            dto.setId(order.getId());
            dto.setOrderNumber(order.getOrderNumber());
            dto.setAmount(order.getAmount());
            // 设置其他字段...
            orderDtoList.add(dto);
        }

        return orderDtoList;

    }

    // 根据Id查询单个订单
    public OrderDto getOrderById(Long id) {

        OrderDto orderDto = new OrderDto();
        Order order = orderRepository.getOrderById(id);
        orderDto.setId(order.getId());
        orderDto.setOrderNumber(order.getOrderNumber());
        orderDto.setAmount(order.getAmount());

        return orderDto;

    }

}
