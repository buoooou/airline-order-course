package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 提供订单的CRUD操作以及支付、取消、出票等业务操作
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "订单管理", description = "订单管理相关接口")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    /**
     * 获取所有订单列表
     * @return 订单列表
     */
    @GetMapping
    @Operation(summary = "获取所有订单", description = "获取系统中所有订单的列表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * 根据ID获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单的详细信息")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    /**
     * 创建新订单
     * @param orderDto 订单数据
     * @return 创建的订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "创建一个新的订单")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public OrderDto createOrder(@RequestBody OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }

    /**
     * 更新订单信息
     * @param id 订单ID
     * @param orderDto 更新后的订单数据
     * @return 更新后的订单
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新订单", description = "更新指定订单的信息（仅管理员可用）")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return orderService.updateOrder(id, orderDto);
    }

    /**
     * 删除订单
     * @param id 订单ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单", description = "删除指定的订单（仅管理员可用）")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    /**
     * 订单支付
     * @param id 订单ID
     * @return 支付后的订单
     */
    @PostMapping("/{id}/pay")
    @Operation(summary = "订单支付", description = "对指定订单进行支付操作")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<OrderDto> pay(@PathVariable Long id) {
        try {
            OrderDto order = orderService.payOrder(id);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 取消订单
     * @param id 订单ID
     * @return 取消后的订单
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消指定的订单")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<OrderDto> cancel(@PathVariable Long id) {
        try {
            OrderDto order = orderService.cancelOrder(id);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 重试出票
     * @param id 订单ID
     * @return 空响应
     */
    @PostMapping("/{id}/retry-ticketing")
    @Operation(summary = "重试出票", description = "对出票失败的订单进行重试出票")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> retryTicketing(@PathVariable Long id) {
        orderService.requestTicketIssuance(id);
        return ResponseEntity.accepted().build();
    }

    /**
     * 获取票务状态
     * @param id 订单ID
     * @return 票务状态信息
     */
    @GetMapping("/{id}/ticket-status")
    @Operation(summary = "获取票务状态", description = "查询订单的票务处理状态")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getTicketStatus(@PathVariable Long id) {
        try {
            String status = orderService.queryTicketStatus(id);
            return ResponseEntity.ok(Map.of(
                "orderId", id,
                "ticketStatus", status
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}