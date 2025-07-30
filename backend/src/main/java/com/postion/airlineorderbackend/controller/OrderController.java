package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.OrderRequest;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的航空订单")
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody OrderRequest request) {
        try {
            String username = getCurrentUsername();
            logger.info("[createOrder] 请求用户: {}", username);
            Order order = orderService.createOrder(request, username);
            logger.info("[createOrder] 订单创建成功, 订单ID: {}", order.getId());
            return ResponseEntity.ok(ApiResponse.success("订单创建成功", order));
        } catch (Exception e) {
            logger.error("[createOrder] 异常: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "获取用户订单", description = "获取当前用户的所有订单")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders() {
        try {
            String username = getCurrentUsername();
            logger.info("[getUserOrders] 请求用户: {}", username);
            List<Order> orders = orderService.getUserOrders(username);
            logger.info("[getUserOrders] 返回订单数: {}", orders != null ? orders.size() : null);
            return ResponseEntity.ok(ApiResponse.success("获取订单成功", orders));
        } catch (Exception e) {
            logger.error("[getUserOrders] 异常: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取订单失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详情")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        try {
            String username = getCurrentUsername();
            logger.info("[getOrderById] 请求用户: {}, 订单ID: {}", username, orderId);
            Order order = orderService.getOrderById(orderId, username);
            logger.info("[getOrderById] 订单详情获取成功, 订单ID: {}", order.getId());
            return ResponseEntity.ok(ApiResponse.success("获取订单详情成功", order));
        } catch (Exception e) {
            logger.error("[getOrderById] 异常: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "支付订单", description = "支付指定订单")
    public ResponseEntity<ApiResponse<Order>> payOrder(@PathVariable Long orderId) {
        try {
            String username = getCurrentUsername();
            logger.info("[payOrder] 请求用户: {}, 订单ID: {}", username, orderId);
            Order order = orderService.payOrder(orderId, username);
            logger.info("[payOrder] 订单支付成功, 订单ID: {}", order.getId());
            return ResponseEntity.ok(ApiResponse.success("支付成功", order));
        } catch (Exception e) {
            logger.error("[payOrder] 异常: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消指定订单")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long orderId) {
        try {
            String username = getCurrentUsername();
            logger.info("[cancelOrder] 请求用户: {}, 订单ID: {}", username, orderId);
            Order order = orderService.cancelOrder(orderId, username);
            logger.info("[cancelOrder] 订单取消成功, 订单ID: {}", order.getId());
            return ResponseEntity.ok(ApiResponse.success("订单取消成功", order));
        } catch (Exception e) {
            logger.error("[cancelOrder] 异常: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/retry-ticketing")
    @Operation(summary = "重试出票", description = "重新尝试出票")
    public ResponseEntity<ApiResponse<Order>> retryTicketing(@PathVariable Long orderId) {
        try {
            String username = getCurrentUsername();
            logger.info("[retryTicketing] 请求用户: {}, 订单ID: {}", username, orderId);
            Order order = orderService.retryTicketing(orderId, username);
            logger.info("[retryTicketing] 重试出票成功, 订单ID: {}", order.getId());
            return ResponseEntity.ok(ApiResponse.success("重试出票成功", order));
        } catch (Exception e) {
            logger.error("[retryTicketing] 异常: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
} 