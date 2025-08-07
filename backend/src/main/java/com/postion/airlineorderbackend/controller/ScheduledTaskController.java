package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.service.IAuthService;
import com.postion.airlineorderbackend.service.IOrderService;
import com.postion.airlineorderbackend.service.ScheduledTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 定时任务管理控制器
 * 提供定时任务的监控和手动触发功能
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/admin/scheduled-tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "定时任务管理", description = "定时任务监控和管理API（管理员专用）")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScheduledTaskController {
    
    private final ScheduledTaskService scheduledTaskService;
    private final IOrderService orderService;
    private final IAuthService authService;
    
    @Value("${app.order.payment-timeout-minutes:30}")
    private int paymentTimeoutMinutes;
    
    @Value("${app.order.ticketing-timeout-minutes:60}")
    private int ticketingTimeoutMinutes;
    
    @Value("${app.order.ticketing-failed-timeout-hours:24}")
    private int ticketingFailedTimeoutHours;
    
    @Value("${app.scheduled.enabled:true}")
    private boolean scheduledEnabled;
    
    /**
     * 获取定时任务配置信息
     */
    @GetMapping("/config")
    @Operation(summary = "获取定时任务配置", description = "获取当前定时任务的配置信息")
    public ApiResponse<Map<String, Object>> getTaskConfig(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.debug("获取定时任务配置请求");
        
        // 验证管理员权限
        validateAdminUser(authorizationHeader);
        
        Map<String, Object> config = new HashMap<>();
        config.put("paymentTimeoutMinutes", paymentTimeoutMinutes);
        config.put("ticketingTimeoutMinutes", ticketingTimeoutMinutes);
        config.put("ticketingFailedTimeoutHours", ticketingFailedTimeoutHours);
        config.put("scheduledEnabled", scheduledEnabled);
        config.put("currentTime", LocalDateTime.now());
        config.put("description", scheduledTaskService.getTaskConfiguration());
        
        return ApiResponse.success("获取定时任务配置成功", config);
    }
    
    /**
     * 手动触发取消超时待支付订单任务
     */
    @PostMapping("/cancel-timeout-payment-orders")
    @Operation(summary = "手动取消超时待支付订单", description = "立即执行取消超时待支付订单的任务")
    public ApiResponse<Map<String, Object>> manualCancelTimeoutPaymentOrders(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("手动触发取消超时待支付订单任务");
        
        // 验证管理员权限
        validateAdminUser(authorizationHeader);
        
        try {
            // 手动执行任务
            scheduledTaskService.cancelTimeoutPaymentOrders();
            
            Map<String, Object> result = new HashMap<>();
            result.put("executionTime", LocalDateTime.now());
            result.put("taskType", "cancelTimeoutPaymentOrders");
            result.put("status", "completed");
            
            return ApiResponse.success("手动取消超时待支付订单任务执行成功", result);
            
        } catch (Exception e) {
            log.error("手动执行取消超时待支付订单任务失败", e);
            throw BusinessException.badRequest("任务执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发处理超时出票订单任务
     */
    @PostMapping("/handle-timeout-ticketing-orders")
    @Operation(summary = "手动处理超时出票订单", description = "立即执行处理超时出票订单的任务")
    public ApiResponse<Map<String, Object>> manualHandleTimeoutTicketingOrders(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("手动触发处理超时出票订单任务");
        
        // 验证管理员权限
        validateAdminUser(authorizationHeader);
        
        try {
            // 手动执行任务
            scheduledTaskService.handleTimeoutTicketingOrders();
            
            Map<String, Object> result = new HashMap<>();
            result.put("executionTime", LocalDateTime.now());
            result.put("taskType", "handleTimeoutTicketingOrders");
            result.put("status", "completed");
            
            return ApiResponse.success("手动处理超时出票订单任务执行成功", result);
            
        } catch (Exception e) {
            log.error("手动执行处理超时出票订单任务失败", e);
            throw BusinessException.badRequest("任务执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发取消长时间出票失败订单任务
     */
    @PostMapping("/cancel-long-time-failed-orders")
    @Operation(summary = "手动取消长时间出票失败订单", description = "立即执行取消长时间出票失败订单的任务")
    public ApiResponse<Map<String, Object>> manualCancelLongTimeFailedOrders(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("手动触发取消长时间出票失败订单任务");
        
        // 验证管理员权限
        validateAdminUser(authorizationHeader);
        
        try {
            // 手动执行任务
            scheduledTaskService.cancelLongTimeTicketingFailedOrders();
            
            Map<String, Object> result = new HashMap<>();
            result.put("executionTime", LocalDateTime.now());
            result.put("taskType", "cancelLongTimeTicketingFailedOrders");
            result.put("status", "completed");
            
            return ApiResponse.success("手动取消长时间出票失败订单任务执行成功", result);
            
        } catch (Exception e) {
            log.error("手动执行取消长时间出票失败订单任务失败", e);
            throw BusinessException.badRequest("任务执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发每日维护任务
     */
    @PostMapping("/daily-maintenance")
    @Operation(summary = "手动执行每日维护任务", description = "立即执行每日维护任务")
    public ApiResponse<Map<String, Object>> manualDailyMaintenance(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("手动触发每日维护任务");
        
        // 验证管理员权限
        validateAdminUser(authorizationHeader);
        
        try {
            // 手动执行任务
            scheduledTaskService.dailyMaintenanceTask();
            
            Map<String, Object> result = new HashMap<>();
            result.put("executionTime", LocalDateTime.now());
            result.put("taskType", "dailyMaintenanceTask");
            result.put("status", "completed");
            
            return ApiResponse.success("手动执行每日维护任务成功", result);
            
        } catch (Exception e) {
            log.error("手动执行每日维护任务失败", e);
            throw BusinessException.badRequest("任务执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取定时任务执行统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取定时任务统计", description = "获取定时任务执行的统计信息")
    public ApiResponse<Map<String, Object>> getTaskStatistics(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.debug("获取定时任务统计请求");
        
        // 验证管理员权限
        validateAdminUser(authorizationHeader);
        
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 获取订单统计信息
            statistics.put("orderStatistics", orderService.getOrderStatistics());
            statistics.put("currentTime", LocalDateTime.now());
            statistics.put("systemStatus", "running");
            
            // 添加定时任务相关统计
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("paymentTimeoutMinutes", paymentTimeoutMinutes);
            taskInfo.put("ticketingTimeoutMinutes", ticketingTimeoutMinutes);
            taskInfo.put("ticketingFailedTimeoutHours", ticketingFailedTimeoutHours);
            taskInfo.put("scheduledEnabled", scheduledEnabled);
            
            statistics.put("taskConfiguration", taskInfo);
            
            return ApiResponse.success("获取定时任务统计成功", statistics);
            
        } catch (Exception e) {
            log.error("获取定时任务统计失败", e);
            throw BusinessException.badRequest("获取统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取系统健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "获取系统健康状态", description = "获取定时任务系统的健康状态")
    public ApiResponse<Map<String, Object>> getSystemHealth(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.debug("获取系统健康状态请求");
        
        // 验证管理员权限
        validateAdminUser(authorizationHeader);
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("scheduledTasksEnabled", scheduledEnabled);
        health.put("databaseConnected", true); // 简化实现，实际可以检查数据库连接
        health.put("shedlockEnabled", true);
        
        // 添加版本信息
        Map<String, String> version = new HashMap<>();
        version.put("application", "airline-order-backend");
        version.put("shedlock", "4.43.0");
        health.put("version", version);
        
        return ApiResponse.success("获取系统健康状态成功", health);
    }
    
    /**
     * 验证管理员用户权限
     */
    private void validateAdminUser(String authorizationHeader) {
        Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
        if (currentUserOpt.isEmpty()) {
            throw BusinessException.unauthorized("未认证或令牌无效");
        }
        
        UserDTO currentUser = currentUserOpt.get();
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw BusinessException.forbidden("需要管理员权限");
        }
    }
}
