package com.position.airlineorderbackend.controller;

import com.position.airlineorderbackend.service.OrderService;
import com.position.airlineorderbackend.dto.OrderDto;
import com.position.airlineorderbackend.annotation.RequireAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "订单管理", description = "订单相关的API接口")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "获取所有订单", description = "获取系统中的所有订单列表，需要ADMIN权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取订单列表"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping
    @RequireAuth(value = "ADMIN", loginRequired = true)
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Operation(summary = "根据ID获取订单", description = "根据订单ID获取特定订单详情，需要USER权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取订单详情"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @GetMapping("/{id}")
    @RequireAuth(value = "USER", loginRequired = true)
    public OrderDto getOrderById(@Parameter(description = "订单ID") @PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
