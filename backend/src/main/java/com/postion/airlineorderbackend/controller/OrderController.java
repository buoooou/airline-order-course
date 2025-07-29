package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.dto.OrderCreateDTO;
import com.postion.airlineorderbackend.dto.OrderResponseDTO;
import com.postion.airlineorderbackend.dto.OrderUpdateDTO;
import com.postion.airlineorderbackend.dto.OrderDTOConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.service.UserService;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单相关的API接口")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderDTOConverter orderDTOConverter;

    @Operation(summary = "获取所有订单", description = "管理员权限，获取系统中的所有订单")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取订单列表",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orderDTOConverter.toResponseDTOList(orders));
    }

    @Operation(summary = "获取当前用户的订单", description = "获取当前登录用户的所有订单")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户订单列表",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {
        // 从 Spring Security 上下文中获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.findByUsername(username);
        List<Order> orders = orderService.getCurrentUserOrders(currentUser);
        return ResponseEntity.ok(orderDTOConverter.toResponseDTOList(orders));
    }

    @Operation(summary = "根据ID获取订单详情", description = "根据订单ID获取订单的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取订单详情",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(orderDTOConverter.toResponseDTO(order));
    }

    @Operation(summary = "创建新订单", description = "创建新的订单，需要提供订单金额")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "订单创建成功",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Parameter(description = "订单创建信息", required = true) 
            @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        // 从 Spring Security 上下文中获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Order order = orderDTOConverter.toEntity(orderCreateDTO, currentUser);
        Order createdOrder = orderService.createOrder(order);
        return new ResponseEntity<>(orderDTOConverter.toResponseDTO(createdOrder), HttpStatus.CREATED);
    }

    @Operation(summary = "支付订单", description = "对指定订单进行支付操作")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "支付成功",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "订单状态不允许支付"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponseDTO> payOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        Order order = orderService.payOrder(id);
        return ResponseEntity.ok(orderDTOConverter.toResponseDTO(order));
    }

    @Operation(summary = "取消订单", description = "取消指定的订单")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取消成功",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "订单状态不允许取消"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(orderDTOConverter.toResponseDTO(order));
    }

    @Operation(summary = "重试出票", description = "对出票失败的订单进行重试出票操作")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "重试出票成功",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "订单状态不允许重试出票"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PostMapping("/{id}/retry-ticketing")
    public ResponseEntity<OrderResponseDTO> retryTicketing(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        Order order = orderService.retryTicketing(id);
        return ResponseEntity.ok(orderDTOConverter.toResponseDTO(order));
    }

    @Operation(summary = "按状态筛选订单", description = "根据订单状态筛选订单列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取订单列表",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "状态参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @Parameter(description = "订单状态", required = true) @PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orderDTOConverter.toResponseDTOList(orders));
    }

    @Operation(summary = "更新订单", description = "更新指定订单的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id,
            @Parameter(description = "订单更新信息", required = true) @Valid @RequestBody OrderUpdateDTO orderUpdateDTO) {
        Order order = orderService.getOrderById(id);
        orderDTOConverter.updateEntityFromDTO(order, orderUpdateDTO);
        Order updatedOrder = orderService.updateOrder(order);
        return ResponseEntity.ok(orderDTOConverter.toResponseDTO(updatedOrder));
    }

    @Operation(summary = "删除订单", description = "删除指定的订单（仅管理员权限）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "订单不存在"),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}