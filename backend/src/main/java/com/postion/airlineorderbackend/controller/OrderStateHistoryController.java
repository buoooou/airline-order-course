package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.entity.OrderStateHistory;
import com.postion.airlineorderbackend.service.OrderStateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderStateHistoryController {

    private final OrderStateHistoryService orderStateHistoryService;

    /**
     * 获取订单的状态转换历史（管理员权限）
     */
    @GetMapping("/{orderId}/state-history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderStateHistory>> getOrderStateHistory(@PathVariable Long orderId) {
        List<OrderStateHistory> history = orderStateHistoryService.getOrderStateHistory(orderId);
        return ResponseEntity.ok(history);
    }

    /**
     * 获取订单的失败状态转换记录（管理员权限）
     */
    @GetMapping("/{orderId}/failed-transitions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderStateHistory>> getFailedTransitions(@PathVariable Long orderId) {
        List<OrderStateHistory> failedTransitions = orderStateHistoryService.getFailedTransitions(orderId);
        return ResponseEntity.ok(failedTransitions);
    }

    /**
     * 获取所有失败的状态转换记录（管理员权限）
     */
    @GetMapping("/failed-transitions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderStateHistory>> getAllFailedTransitions() {
        List<OrderStateHistory> allFailedTransitions = orderStateHistoryService.getAllFailedTransitions();
        return ResponseEntity.ok(allFailedTransitions);
    }
}