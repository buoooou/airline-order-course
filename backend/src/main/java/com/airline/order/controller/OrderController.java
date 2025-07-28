package com.airline.order.controller;

import com.airline.order.dto.CreateOrderRequest;
import com.airline.order.dto.OrderDTO;
import com.airline.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 订单控制器
 * 处理订单的查询、新建、取消、修改等操作
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建新订单
     * @param request 创建订单请求
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody CreateOrderRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            OrderDTO orderDTO = orderService.createOrder(request);
            
            response.put("success", true);
            response.put("message", "订单创建成功");
            response.put("order", orderDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 查询订单列表
     * @param userId 用户ID（可选）
     * @param status 订单状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<OrderDTO> orderPage = orderService.getOrders(userId, status, page, size);
            
            response.put("success", true);
            response.put("orders", orderPage.getContent());
            response.put("totalElements", orderPage.getTotalElements());
            response.put("totalPages", orderPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 根据ID获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            OrderDTO orderDTO = orderService.getOrderById(orderId);
            
            response.put("success", true);
            response.put("order", orderDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取订单详情失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消结果
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            OrderDTO orderDTO = orderService.cancelOrder(orderId);
            
            response.put("success", true);
            response.put("message", "订单已取消");
            response.put("order", orderDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "取消订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 修改订单状态
     * @param orderId 订单ID
     * @param statusRequest 状态修改请求
     * @return 修改结果
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> statusRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String newStatusStr = statusRequest.get("status");
            OrderDTO orderDTO = orderService.updateOrderStatus(orderId, newStatusStr);
            
            response.put("success", true);
            response.put("message", "订单状态已更新");
            response.put("order", orderDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新订单状态失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 订单信息
     */
    @GetMapping("/by-number/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getOrderByNumber(@PathVariable String orderNumber) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            OrderDTO orderDTO = orderService.getOrderByNumber(orderNumber);
            
            response.put("success", true);
            response.put("order", orderDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询订单失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}