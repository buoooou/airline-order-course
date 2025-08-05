package com.position.airlineorderbackend.controller;

import com.position.airlineorderbackend.service.OrderStateLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shedlock-test")
public class ShedLockTestController {

    @Autowired
    private OrderStateLockService lockService;

    /**
     * 测试订单状态锁
     */
    @GetMapping("/test-lock/{orderId}")
    public String testOrderLock(@PathVariable Long orderId) {
        try {
            String result = lockService.executeWithLock(orderId, () -> {
                // 模拟耗时操作
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "订单 " + orderId + " 状态更新成功";
            });
            return result;
        } catch (Exception e) {
            return "操作失败: " + e.getMessage();
        }
    }

    /**
     * 检查订单是否被锁定
     */
    @GetMapping("/check-lock/{orderId}")
    public String checkOrderLock(@PathVariable Long orderId) {
        boolean isLocked = lockService.isLocked(orderId);
        return "订单 " + orderId + " 锁定状态: " + (isLocked ? "已锁定" : "未锁定");
    }

    /**
     * 测试并发锁
     */
    @PostMapping("/concurrent-test/{orderId}")
    public String testConcurrentLock(@PathVariable Long orderId) {
        // 这里可以模拟并发请求
        return "并发测试请求已提交，订单ID: " + orderId;
    }
} 