package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderCreateRequest;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.dto.OrderUpdateRequest;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.enums.OrderStatus;
import com.postion.airlineorderbackend.service.AuthService;
import com.postion.airlineorderbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 订单控制器
 * 提供订单管理相关的REST API
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
    
    private final OrderService orderService;
    private final AuthService authService;
    
    /**
     * 创建新订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的航班订单")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderCreateRequest createRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("创建订单请求: 用户ID={}, 航班ID={}", createRequest.getUserId(), createRequest.getFlightInfoId());
        
        try {
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            if (!currentUser.getRole().name().equals("ADMIN") && 
                !currentUser.getId().equals(createRequest.getUserId())) {
                
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "无权限为其他用户创建订单");
                error.put("error", "PERMISSION_DENIED");
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            OrderDTO orderDTO = orderService.createOrder(createRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("data", orderDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("订单创建失败: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", "INVALID_REQUEST");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            log.error("订单创建异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "订单创建失败");
            error.put("error", "CREATE_ORDER_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 根据ID获取订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.debug("获取订单详情请求: {}", id);
        
        try {
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            Optional<OrderDTO> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "订单不存在");
                error.put("error", "ORDER_NOT_FOUND");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            OrderDTO order = orderOpt.get();
            
            if (!currentUser.getRole().name().equals("ADMIN") && 
                !currentUser.getId().equals(order.getUserId())) {
                
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "无权限访问此订单");
                error.put("error", "PERMISSION_DENIED");
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取订单详情成功");
            result.put("data", order);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("获取订单详情异常: {}", id, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取订单详情失败");
            error.put("error", "GET_ORDER_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 获取当前用户的订单列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的订单", description = "获取当前用户的订单列表")
    public ResponseEntity<?> getMyOrders(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sort) {
        
        log.debug("获取我的订单请求: page={}, size={}", page, size);
        
        try {
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
            Page<OrderDTO> orders = orderService.findByUserId(currentUser.getId(), pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取订单列表成功");
            result.put("data", orders);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("获取我的订单异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取订单列表失败");
            error.put("error", "GET_ORDERS_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态", description = "更新订单的状态信息")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateRequest updateRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("更新订单状态请求: 订单ID={}, 新状态={}", id, updateRequest.getStatus());
        
        try {
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            if (!currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "无权限更新订单状态");
                error.put("error", "PERMISSION_DENIED");
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, updateRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单状态更新成功");
            result.put("data", updatedOrder);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("订单状态更新失败: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", "INVALID_STATUS_TRANSITION");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            log.error("订单状态更新异常: {}", id, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "订单状态更新失败");
            error.put("error", "UPDATE_STATUS_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消指定的订单")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("取消订单请求: 订单ID={}, 原因={}", id, reason);
        
        try {
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            Optional<OrderDTO> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "订单不存在");
                error.put("error", "ORDER_NOT_FOUND");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            OrderDTO order = orderOpt.get();
            
            if (!currentUser.getRole().name().equals("ADMIN") && 
                !currentUser.getId().equals(order.getUserId())) {
                
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "无权限取消此订单");
                error.put("error", "PERMISSION_DENIED");
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            String cancelReason = reason != null ? reason : "用户主动取消";
            OrderDTO cancelledOrder = orderService.cancelOrder(id, cancelReason);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单取消成功");
            result.put("data", cancelledOrder);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("订单取消失败: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", "CANCEL_NOT_ALLOWED");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            log.error("订单取消异常: {}", id, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "订单取消失败");
            error.put("error", "CANCEL_ORDER_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 支付订单
     */
    @PutMapping("/{id}/pay")
    @Operation(summary = "支付订单", description = "支付指定的订单")
    public ResponseEntity<?> payOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("支付订单请求: 订单ID={}", id);
        
        try {
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            Optional<OrderDTO> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "订单不存在");
                error.put("error", "ORDER_NOT_FOUND");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            OrderDTO order = orderOpt.get();
            
            if (!currentUser.getRole().name().equals("ADMIN") && 
                !currentUser.getId().equals(order.getUserId())) {
                
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "无权限支付此订单");
                error.put("error", "PERMISSION_DENIED");
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            OrderDTO paidOrder = orderService.payOrder(id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单支付成功");
            result.put("data", paidOrder);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("订单支付失败: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", "PAYMENT_NOT_ALLOWED");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            log.error("订单支付异常: {}", id, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "订单支付失败");
            error.put("error", "PAY_ORDER_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 创建未认证响应
     */
    private ResponseEntity<?> createUnauthorizedResponse() {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "未认证或令牌无效");
        error.put("error", "UNAUTHORIZED");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
