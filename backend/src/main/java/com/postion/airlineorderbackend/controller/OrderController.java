package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.entity.Result;
import com.postion.airlineorderbackend.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "订单管理", description = "订单接口类")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;

    // 获取所有订单
    @Operation(summary = "获取所有订单", description = "返回系统中所有的订单信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取订单列表"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public Result getAllOrders() {
        return Result.success(orderService.getAllOrders());
    }

    // 获取特定用户名下所有订单
    @Operation(summary = "获取用户订单", description = "根据用户ID返回该用户的所有订单")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取用户订单列表"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/user/{userId}")
    public Result getAllOrdersByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        return Result.success(orderService.getAllOrdersByUserId(userId));
    }

    // 根据ID获取单个订单
    @Operation(summary = "获取单个订单", description = "根据订单ID返回订单详情")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取订单详情"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public Result getOrderById(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        return Result.success(orderService.getOrderById(id));
    }
}
