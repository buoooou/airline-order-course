package com.postion.airlineorderbackend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    
	@GetMapping
    @Operation(summary = "获取当前用户的所有订单", description = "获取当前登录用户的所有订单")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getAllOrders() {
        try {
            List<OrderDto> orders = orderService.getAllOrders();
            return ResponseEntity.ok(ApiResponse.success("成功获取所有订单", orders));
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过订单ID获取订单", description = "获取当前订单ID的订单")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Long id) {
        try {
            OrderDto order = orderService.getOrderById(id);
            return ResponseEntity.ok(ApiResponse.success("成功获取订单", order));
        } catch (Exception e) {
            throw e;
        }
    }
    
}
