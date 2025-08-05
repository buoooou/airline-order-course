package com.postion.airlineorderbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "获取所有订单", description = "返回系统中所有的订单列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功获取订单列表"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<OrderDto> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取订单", description = "根据订单ID查询订单详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功获取订单详情"),
            @ApiResponse(responseCode = "404", description = "订单不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public OrderDto getOrderById(@PathVariable Long id){
        return orderService.getOrderById(id);
    }

}
