package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderItemDto;
import com.postion.airlineorderbackend.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public List<OrderItemDto> getAllOrderItems() {
        return orderItemService.getAllOrderItems();
    }
}