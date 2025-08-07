package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OrderController {

    private final OrderService orderService;

    /**
     * 获取所有订单
     * 
     * @return 订单列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getAllOrders() {
        try {
            List<OrderDto> orders = orderService.getAllOrders();
            return ResponseEntity.ok(ApiResponse.success("成功获取所有订单", orders));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据ID获取订单
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Long id) {
        try {
            OrderDto order = orderService.getOrderById(id);
            return ResponseEntity.ok(ApiResponse.success("成功获取订单", order));
        } catch (Exception e) {
            throw e;
        }
    }
}