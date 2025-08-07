package com.airline.order.controller;

import com.airline.order.dto.ApiResponse;
import com.airline.order.dto.CreateOrderRequest;
import com.airline.order.dto.OrderDTO;
import com.airline.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ApiResponse<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO orderDTO = orderService.createOrder(request);
        return ApiResponse.success("订单创建成功", orderDTO);
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
    public ApiResponse<Map<String, Object>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<OrderDTO> orderPage = orderService.getOrders(userId, status, page, size);
        
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("orders", orderPage.getContent());
        pageData.put("totalElements", orderPage.getTotalElements());
        pageData.put("totalPages", orderPage.getTotalPages());
        pageData.put("currentPage", page);
        pageData.put("pageSize", size);
        
        return ApiResponse.success(pageData);
    }
    
    /**
     * 根据ID获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ApiResponse.success(orderDTO);
    }
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消结果
     */
    @PutMapping("/{orderId}/cancel")
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.cancelOrder(orderId);
        return ApiResponse.success("订单已取消", orderDTO);
    }
    
    /**
     * 修改订单状态
     * @param orderId 订单ID
     * @param statusRequest 状态修改请求
     * @return 修改结果
     */
    @PutMapping("/{orderId}/status")
    public ApiResponse<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> statusRequest) {
        
        String newStatusStr = statusRequest.get("status");
        OrderDTO orderDTO = orderService.updateOrderStatus(orderId, newStatusStr);
        return ApiResponse.success("订单状态已更新", orderDTO);
    }
    
    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 订单信息
     */
    @GetMapping("/by-number/{orderNumber}")
    public ApiResponse<OrderDTO> getOrderByNumber(@PathVariable String orderNumber) {
        OrderDTO orderDTO = orderService.getOrderByNumber(orderNumber);
        return ApiResponse.success(orderDTO);
    }
    
}