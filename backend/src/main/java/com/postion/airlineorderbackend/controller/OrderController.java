package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.OrderRequest;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单相关操作接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的航空订单")
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody OrderRequest request) {
        try {
            String username = getCurrentUsername();
            Order order = orderService.createOrder(request, username);
            return ResponseEntity.ok(ApiResponse.success("订单创建成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "获取用户订单", description = "获取当前用户的所有订单")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders() {
        try {
            String username = getCurrentUsername();
            List<Order> orders = orderService.getUserOrders(username);
            return ResponseEntity.ok(ApiResponse.success("获取订单成功", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详情")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        try {
            String username = getCurrentUsername();
            Order order = orderService.getOrderById(orderId, username);
            return ResponseEntity.ok(ApiResponse.success("获取订单详情成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "支付订单", description = "支付指定订单")
    public ResponseEntity<ApiResponse<Order>> payOrder(@PathVariable Long orderId) {
        try {
            String username = getCurrentUsername();
            Order order = orderService.payOrder(orderId, username);
            return ResponseEntity.ok(ApiResponse.success("支付成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消指定订单")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long orderId) {
        try {
            String username = getCurrentUsername();
            Order order = orderService.cancelOrder(orderId, username);
            return ResponseEntity.ok(ApiResponse.success("订单取消成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/retry-ticketing")
    @Operation(summary = "重试出票", description = "重新尝试出票")
    public ResponseEntity<ApiResponse<Order>> retryTicketing(@PathVariable Long orderId) {
        try {
            String username = getCurrentUsername();
            Order order = orderService.retryTicketing(orderId, username);
            return ResponseEntity.ok(ApiResponse.success("重试出票成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
} 