package com.position.airlineorderbackend.controller;

import com.position.airlineorderbackend.model.OrderStatus;
import com.position.airlineorderbackend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/{id}/actions")
public class OrderActionController {
    
    private final OrderService orderService;
    
    public OrderActionController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    // 支付操作：PENDING_PAYMENT -> PAID
    @PostMapping("/pay")
    public ResponseEntity<String> payOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.payOrder(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // 开始出票：PAID -> TICKETING_IN_PROGRESS
    @PostMapping("/start-ticketing")
    public ResponseEntity<String> startTicketing(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.startTicketing(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // 出票成功：TICKETING_IN_PROGRESS -> TICKETED
    @PostMapping("/complete-ticketing")
    public ResponseEntity<String> completeTicketing(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.completeTicketing(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // 出票失败：TICKETING_IN_PROGRESS -> TICKETING_IN_FAILED
    @PostMapping("/fail-ticketing")
    public ResponseEntity<String> failTicketing(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.failTicketing(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // 取消订单：多种状态 -> CANCELED
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.cancelOrder(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // 重新支付：TICKETING_IN_FAILED -> PAID (重新进入支付流程)
    @PostMapping("/retry-payment")
    public ResponseEntity<String> retryPayment(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.retryPayment(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // 重新出票：PAID -> TICKETING_IN_PROGRESS (重新进入出票流程)
    @PostMapping("/retry-ticketing")
    public ResponseEntity<String> retryTicketing(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.retryTicketing(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
