package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.statemachine.OrderState;
import com.postion.airlineorderbackend.statemachine.OrderStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderDebugController {

    private final OrderRepository orderRepository;
    private final OrderStateMachineService stateMachineService;

    /**
     * 调试订单状态信息
     * 提供详细的订单状态诊断信息
     */
    @GetMapping("/{orderId}/debug")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugOrderState(@PathVariable Long orderId) {
        try {
            Map<String, Object> debugInfo = new HashMap<>();
            
            // 1. 检查订单是否存在
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                debugInfo.put("error", "订单不存在");
                debugInfo.put("orderId", orderId);
                return ResponseEntity.ok(ApiResponse.success("调试信息", debugInfo));
            }

            // 2. 获取数据库状态
            String dbStatus = order.getStatus();
            debugInfo.put("databaseStatus", dbStatus);
            debugInfo.put("databaseStatusDescription", OrderState.valueOf(dbStatus).getDescription());
            
            // 3. 获取状态机状态
            try {
                OrderState machineState = stateMachineService.getCurrentState(orderId);
                debugInfo.put("stateMachineStatus", machineState != null ? machineState.name() : "无法获取");
                debugInfo.put("stateMachineDescription", machineState != null ? machineState.getDescription() : "N/A");
                
                // 4. 检查状态一致性
                boolean isConsistent = machineState != null && machineState.name().equals(dbStatus);
                debugInfo.put("statusConsistent", isConsistent);
                
            } catch (Exception e) {
                debugInfo.put("stateMachineError", e.getMessage());
            }
            
            // 5. 订单基本信息
            debugInfo.put("orderId", order.getId());
            debugInfo.put("orderNumber", order.getOrderNumber());
            debugInfo.put("userId", order.getUserId());
            debugInfo.put("amount", order.getAmount());
            debugInfo.put("creationDate", order.getCreationDate());
            
            return ResponseEntity.ok(ApiResponse.success("调试信息", debugInfo));
            
        } catch (Exception e) {
            log.error("调试订单状态失败: {}", orderId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("调试失败: " + e.getMessage()));
        }
    }

    /**
     * 强制重置订单状态
     * 用于修复状态不一致问题
     */
    @PutMapping("/{orderId}/reset-state")
    public ResponseEntity<ApiResponse<String>> resetOrderState(
            @PathVariable Long orderId, 
            @RequestParam String newState) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("订单不存在"));
            }
            
            // 验证状态是否有效
            try {
                OrderState.valueOf(newState);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("无效的状态: " + newState));
            }
            
            // 强制更新状态
            order.setStatus(newState);
            orderRepository.save(order);
            
            // 清除状态机缓存
            stateMachineService.clearStateMachineCache(orderId);
            
            return ResponseEntity.ok(ApiResponse.success("状态重置成功", newState));
            
        } catch (Exception e) {
            log.error("重置订单状态失败: {}", orderId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("重置失败: " + e.getMessage()));
        }
    }
}