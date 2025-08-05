package com.position.airlineorderbackend.controller;

import com.position.airlineorderbackend.scheduler.OrderScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduler")
@Tag(name = "定时任务测试", description = "定时任务相关的测试接口")
@SecurityRequirement(name = "Bearer Authentication")
public class SchedulerTestController {

    @Autowired
    private OrderScheduler orderScheduler;

    @Operation(summary = "手动触发超时订单检查", description = "手动执行超时订单检查任务")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "任务执行成功"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/check-timeout-orders")
    public ResponseEntity<String> checkTimeoutOrders() {
        try {
            orderScheduler.checkTimeoutOrders();
            return ResponseEntity.ok("超时订单检查任务执行完成");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("任务执行失败: " + e.getMessage());
        }
    }

    @Operation(summary = "手动触发出票失败重试", description = "手动执行出票失败重试任务")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "任务执行成功"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/retry-failed-ticketing")
    public ResponseEntity<String> retryFailedTicketing() {
        try {
            orderScheduler.retryFailedTicketing();
            return ResponseEntity.ok("出票失败重试任务执行完成");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("任务执行失败: " + e.getMessage());
        }
    }

    @Operation(summary = "手动触发订单状态统计", description = "手动执行订单状态统计任务")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "任务执行成功"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/order-statistics")
    public ResponseEntity<String> orderStatistics() {
        try {
            orderScheduler.orderStatusStatistics();
            return ResponseEntity.ok("订单状态统计任务执行完成");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("任务执行失败: " + e.getMessage());
        }
    }

    @Operation(summary = "手动触发长时间出票订单检查", description = "手动执行长时间出票订单检查任务")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "任务执行成功"),
        @ApiResponse(responseCode = "401", description = "未授权访问"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/check-stuck-ticketing")
    public ResponseEntity<String> checkStuckTicketingOrders() {
        try {
            orderScheduler.checkStuckTicketingOrders();
            return ResponseEntity.ok("长时间出票订单检查任务执行完成");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("任务执行失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取定时任务状态", description = "获取所有定时任务的执行状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/status")
    public ResponseEntity<String> getSchedulerStatus() {
        return ResponseEntity.ok("定时任务系统运行正常");
    }
} 