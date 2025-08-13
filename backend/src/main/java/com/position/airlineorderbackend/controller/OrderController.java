package com.position.airlineorderbackend.controller;

import com.position.airlineorderbackend.service.OrderService;
import com.position.airlineorderbackend.dto.OrderDto;
import com.position.airlineorderbackend.dto.CreateOrderRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import com.position.airlineorderbackend.util.SecurityUtils;
import com.position.airlineorderbackend.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "订单管理", description = "订单相关的API接口")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    @Autowired
    public OrderController(OrderService orderService, SecurityUtils securityUtils) {
        this.orderService = orderService;
        this.securityUtils = securityUtils;
    }

    @Operation(summary = "创建订单", description = "创建新的航空订单")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "订单创建成功", 
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "404", description = "用户或航班信息不存在")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Long currentUserId = securityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new ResourceNotFoundException("用户", "认证信息", "未找到");
        }
        
        OrderDto orderDto = orderService.createOrder(request, currentUserId);
        return ResponseEntity.ok(orderDto);
    }

    @Operation(summary = "获取所有订单", description = "获取系统中的所有订单列表，需要ADMIN权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取订单列表"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Operation(summary = "获取我的订单", description = "获取当前登录用户的订单列表，需要USER或ADMIN权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取订单列表"),
        @ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<OrderDto> getMyOrders() {
        Long currentUserId = securityUtils.getCurrentUserId();
        return orderService.getOrdersForUser(currentUserId);
    }

    @Operation(summary = "根据ID获取订单", description = "根据订单ID获取特定订单详情，需要USER权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取订单详情"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public OrderDto getOrderById(@Parameter(description = "订单ID") @PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
