package com.postion.airlineorderbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    @Autowired
    private OrderService orderService;

    // 每5分钟检查一次过期订单
    @Scheduled(fixedRate = 300000)
    public void cancelExpiredOrders() {
        try {
            orderService.cancelExpiredOrders();
            System.out.println("Scheduled task: Expired orders cancelled");
        } catch (Exception e) {
            System.err.println("Error in scheduled task: " + e.getMessage());
        }
    }

    // 每天凌晨2点执行清理任务
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyCleanup() {
        try {
            // 这里可以添加其他清理逻辑
            System.out.println("Scheduled task: Daily cleanup completed");
        } catch (Exception e) {
            System.err.println("Error in daily cleanup: " + e.getMessage());
        }
    }
} 