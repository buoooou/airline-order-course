package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.OrderCreateRequest;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.dto.OrderUpdateRequest;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.enums.OrderStatus;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.service.IAuthService;
import com.postion.airlineorderbackend.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 订单控制器 - 简化版本
 * 使用统一异常处理和响应格式
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "订单管理", description = "订单相关API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {
    
    private final IOrderService orderService;
    private final IAuthService authService;
    
    /**
     * 获取所有订单列表（管理员专用）
     */
    @GetMapping
    @Operation(summary = "获取所有订单", description = "获取系统中所有订单列表（管理员专用）")
    public ApiResponse<List<OrderDTO>> getAllOrders(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sort,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId) {
        
        log.debug("获取所有订单请求: page={}, size={}, status={}, userId={}", page, size, status, userId);
        
        // 验证用户身份
        UserDTO currentUser = validateUser(authorizationHeader);
        
        // 只有管理员可以查看所有订单
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw BusinessException.forbidden("无权限访问所有订单");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<OrderDTO> orders;
        
        if (userId != null) {
            orders = orderService.findByUserId(userId, pageable);
        } else if (status != null) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.findByStatus(orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw BusinessException.badRequest("无效的订单状态: " + status);
            }
        } else {
            orders = orderService.findByMultipleConditions(null, null, null, null, null, pageable);
        }
        
        return ApiResponse.success("获取订单列表成功", orders.getContent(), 
                ApiResponse.PaginationInfo.of(orders));
    }
    
    /**
     * 获取当前用户的订单列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的订单", description = "获取当前用户的订单列表")
    public ApiResponse<List<OrderDTO>> getMyOrders(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sort) {
        
        log.debug("获取我的订单请求: page={}, size={}", page, size);
        
        UserDTO currentUser = validateUser(authorizationHeader);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<OrderDTO> orders = orderService.findByUserId(currentUser.getId(), pageable);
        
        return ApiResponse.success("获取订单列表成功", orders.getContent(), 
                ApiResponse.PaginationInfo.of(orders));
    }
    
    /**
     * 根据ID获取订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    public ApiResponse<OrderDTO> getOrderById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.debug("获取订单详情请求: {}", id);
        
        UserDTO currentUser = validateUser(authorizationHeader);
        
        Optional<OrderDTO> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) {
            throw BusinessException.orderNotFound();
        }
        
        OrderDTO order = orderOpt.get();
        
        // 权限检查：只有管理员或订单所有者可以查看
        if (!currentUser.getRole().name().equals("ADMIN") && 
            !currentUser.getId().equals(order.getUserId())) {
            throw BusinessException.forbidden("无权限访问此订单");
        }
        
        return ApiResponse.success("获取订单详情成功", order);
    }
    
    /**
     * 创建新订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的航班订单")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderDTO> createOrder(
            @Valid @RequestBody OrderCreateRequest createRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("创建订单请求: 用户ID={}, 航班ID={}", createRequest.getUserId(), createRequest.getFlightInfoId());
        
        UserDTO currentUser = validateUser(authorizationHeader);
        
        // 权限检查：只有管理员或为自己创建订单
        if (!currentUser.getRole().name().equals("ADMIN") && 
            !currentUser.getId().equals(createRequest.getUserId())) {
            throw BusinessException.forbidden("无权限为其他用户创建订单");
        }
        
        OrderDTO orderDTO = orderService.createOrder(createRequest);
        return ApiResponse.success("订单创建成功", orderDTO);
    }
    
    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态", description = "更新订单的状态信息")
    public ApiResponse<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateRequest updateRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("更新订单状态请求: 订单ID={}, 新状态={}", id, updateRequest.getStatus());
        
        UserDTO currentUser = validateUser(authorizationHeader);
        
        // 只有管理员可以更新订单状态
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw BusinessException.forbidden("无权限更新订单状态");
        }
        
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, updateRequest);
        return ApiResponse.success("订单状态更新成功", updatedOrder);
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消指定的订单")
    public ApiResponse<OrderDTO> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("取消订单请求: 订单ID={}, 原因={}", id, reason);
        
        UserDTO currentUser = validateUser(authorizationHeader);
        
        Optional<OrderDTO> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) {
            throw BusinessException.orderNotFound();
        }
        
        OrderDTO order = orderOpt.get();
        
        // 权限检查：只有管理员或订单所有者可以取消
        if (!currentUser.getRole().name().equals("ADMIN") && 
            !currentUser.getId().equals(order.getUserId())) {
            throw BusinessException.forbidden("无权限取消此订单");
        }
        
        String cancelReason = reason != null ? reason : "用户主动取消";
        OrderDTO cancelledOrder = orderService.cancelOrder(id, cancelReason);
        
        return ApiResponse.success("订单取消成功", cancelledOrder);
    }
    
    /**
     * 支付订单
     */
    @PutMapping("/{id}/pay")
    @Operation(summary = "支付订单", description = "支付指定的订单")
    public ApiResponse<OrderDTO> payOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("支付订单请求: 订单ID={}", id);
        
        UserDTO currentUser = validateUser(authorizationHeader);
        
        Optional<OrderDTO> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) {
            throw BusinessException.orderNotFound();
        }
        
        OrderDTO order = orderOpt.get();
        
        // 权限检查：只有管理员或订单所有者可以支付
        if (!currentUser.getRole().name().equals("ADMIN") && 
            !currentUser.getId().equals(order.getUserId())) {
            throw BusinessException.forbidden("无权限支付此订单");
        }
        
        OrderDTO paidOrder = orderService.payOrder(id);
        return ApiResponse.success("订单支付成功", paidOrder);
    }
    
    /**
     * 验证用户身份的辅助方法
     */
    private UserDTO validateUser(String authorizationHeader) {
        Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
        if (currentUserOpt.isEmpty()) {
            throw BusinessException.unauthorized("未认证或令牌无效");
        }
        return currentUserOpt.get();
    }
}
