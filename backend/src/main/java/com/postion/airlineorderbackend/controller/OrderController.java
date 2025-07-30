package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 获取所有订单
    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 获取特定用户名下所有订单
    @GetMapping("/user/{userId}")
    public List<OrderDto> getAllOrdersByUserId(@PathVariable Long userId) {
        return orderService.getAllOrdersByUserId(userId);
    }

    // 根据ID获取单个订单
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
