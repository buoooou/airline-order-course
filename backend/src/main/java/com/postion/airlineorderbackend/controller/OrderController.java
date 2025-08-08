package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.BaseResponse;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.dto.UpdateOrderRequest;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单接口", description = "提供订单的增删改查操作")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 获取所有订单
    @Operation(summary = "获取所有订单")
    @GetMapping
    public ResponseEntity<BaseResponse<List<OrderDTO>>> getAllOrders() {
        List<OrderDTO> list = orderService.getAllOrders();
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, list));
    }

    // 根据ID获取订单详情
    @Operation(summary = "根据 ID 获取订单详情")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在: id=" + id));
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, order));
    }

    @Operation(summary = "更新订单")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderDTO>> updateOrder(@PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
		OrderDTO updated = orderService.updateOrder(id, request)
		.orElseThrow(() -> new RuntimeException("订单更新失败，未找到订单: id=" + id));
		return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, updated));
	}
    
	/*
	 * @Operation(summary = "创建订单")
	 * 
	 * @PostMapping public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody
	 * CreateOrderRequest request) { OrderDTO created =
	 * orderService.createOrder(request); return ResponseEntity.ok(created); }
	 */
}
