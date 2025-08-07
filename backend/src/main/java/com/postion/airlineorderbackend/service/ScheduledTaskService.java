package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务服务类
 * 使用ShedLock确保在分布式环境中定时任务只在一个实例上执行
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {
    
    private final IOrderService orderService;
    
    /**
     * 待支付订单超时时间（分钟）
     * 可通过配置文件调整
     */
    @Value("${app.order.payment-timeout-minutes:30}")
    private int paymentTimeoutMinutes;
    
    /**
     * 出票中订单超时时间（分钟）
     * 可通过配置文件调整
     */
    @Value("${app.order.ticketing-timeout-minutes:60}")
    private int ticketingTimeoutMinutes;
    
    /**
     * 出票失败订单自动取消时间（小时）
     * 可通过配置文件调整
     */
    @Value("${app.order.ticketing-failed-timeout-hours:24}")
    private int ticketingFailedTimeoutHours;
    
    /**
     * 定时处理超时的待支付订单
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟
    @SchedulerLock(
        name = "cancelTimeoutPaymentOrders",
        lockAtMostFor = "4m", // 最长锁定4分钟
        lockAtLeastFor = "1m"  // 最短锁定1分钟
    )
    public void cancelTimeoutPaymentOrders() {
        log.info("开始执行定时任务：取消超时的待支付订单");
        
        try {
            LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(paymentTimeoutMinutes);
            
            // 查找超时的待支付订单
            List<OrderDTO> timeoutOrders = findTimeoutOrdersByStatus(OrderStatus.PENDING_PAYMENT, timeoutTime);
            
            int cancelledCount = 0;
            for (OrderDTO order : timeoutOrders) {
                try {
                    orderService.cancelOrder(order.getId(), 
                        String.format("支付超时自动取消（超时%d分钟）", paymentTimeoutMinutes));
                    cancelledCount++;
                    log.info("自动取消超时待支付订单：{}", order.getOrderNumber());
                } catch (Exception e) {
                    log.error("取消超时待支付订单失败：{}", order.getOrderNumber(), e);
                }
            }
            
            if (cancelledCount > 0) {
                log.info("定时任务完成：取消了{}个超时的待支付订单", cancelledCount);
            } else {
                log.debug("定时任务完成：没有发现超时的待支付订单");
            }
            
        } catch (Exception e) {
            log.error("执行取消超时待支付订单任务时发生异常", e);
        }
    }
    
    /**
     * 定时处理超时的出票中订单
     * 每10分钟执行一次
     */
    @Scheduled(fixedRate = 10 * 60 * 1000) // 10分钟
    @SchedulerLock(
        name = "handleTimeoutTicketingOrders",
        lockAtMostFor = "9m", // 最长锁定9分钟
        lockAtLeastFor = "2m"  // 最短锁定2分钟
    )
    public void handleTimeoutTicketingOrders() {
        log.info("开始执行定时任务：处理超时的出票中订单");
        
        try {
            LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(ticketingTimeoutMinutes);
            
            // 查找超时的出票中订单
            List<OrderDTO> timeoutOrders = findTimeoutOrdersByStatus(OrderStatus.TICKETING_IN_PROGRESS, timeoutTime);
            
            int processedCount = 0;
            for (OrderDTO order : timeoutOrders) {
                try {
                    // 将超时的出票中订单标记为出票失败
                    orderService.updateOrderStatus(order.getId(), 
                        createUpdateRequest(OrderStatus.TICKETING_FAILED, 
                            String.format("出票超时（超时%d分钟）", ticketingTimeoutMinutes)));
                    processedCount++;
                    log.info("自动标记超时出票订单为失败：{}", order.getOrderNumber());
                } catch (Exception e) {
                    log.error("处理超时出票订单失败：{}", order.getOrderNumber(), e);
                }
            }
            
            if (processedCount > 0) {
                log.info("定时任务完成：处理了{}个超时的出票中订单", processedCount);
            } else {
                log.debug("定时任务完成：没有发现超时的出票中订单");
            }
            
        } catch (Exception e) {
            log.error("执行处理超时出票中订单任务时发生异常", e);
        }
    }
    
    /**
     * 定时处理长时间出票失败的订单
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // 1小时
    @SchedulerLock(
        name = "cancelLongTimeTicketingFailedOrders",
        lockAtMostFor = "55m", // 最长锁定55分钟
        lockAtLeastFor = "5m"   // 最短锁定5分钟
    )
    public void cancelLongTimeTicketingFailedOrders() {
        log.info("开始执行定时任务：取消长时间出票失败的订单");
        
        try {
            LocalDateTime timeoutTime = LocalDateTime.now().minusHours(ticketingFailedTimeoutHours);
            
            // 查找长时间出票失败的订单
            List<OrderDTO> timeoutOrders = findTimeoutOrdersByStatus(OrderStatus.TICKETING_FAILED, timeoutTime);
            
            int cancelledCount = 0;
            for (OrderDTO order : timeoutOrders) {
                try {
                    orderService.cancelOrder(order.getId(), 
                        String.format("出票失败超时自动取消（失败%d小时）", ticketingFailedTimeoutHours));
                    cancelledCount++;
                    log.info("自动取消长时间出票失败订单：{}", order.getOrderNumber());
                } catch (Exception e) {
                    log.error("取消长时间出票失败订单失败：{}", order.getOrderNumber(), e);
                }
            }
            
            if (cancelledCount > 0) {
                log.info("定时任务完成：取消了{}个长时间出票失败的订单", cancelledCount);
            } else {
                log.debug("定时任务完成：没有发现长时间出票失败的订单");
            }
            
        } catch (Exception e) {
            log.error("执行取消长时间出票失败订单任务时发生异常", e);
        }
    }
    
    /**
     * 定时清理统计信息和日志
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @SchedulerLock(
        name = "dailyMaintenanceTask",
        lockAtMostFor = "2h", // 最长锁定2小时
        lockAtLeastFor = "10m" // 最短锁定10分钟
    )
    public void dailyMaintenanceTask() {
        log.info("开始执行定时任务：每日维护任务");
        
        try {
            // 统计今日订单处理情况
            generateDailyOrderReport();
            
            // 清理过期的锁记录（可选）
            cleanupExpiredLocks();
            
            log.info("每日维护任务执行完成");
            
        } catch (Exception e) {
            log.error("执行每日维护任务时发生异常", e);
        }
    }
    
    /**
     * 根据状态和时间查找超时订单
     */
    private List<OrderDTO> findTimeoutOrdersByStatus(OrderStatus status, LocalDateTime timeoutTime) {
        // 使用多条件查询找到指定状态且创建时间早于超时时间的订单
        return orderService.findByMultipleConditions(
            null, // orderNumber
            null, // userId
            status, // status
            null, // startTime
            timeoutTime, // endTime - 创建时间早于此时间的订单
            org.springframework.data.domain.PageRequest.of(0, 1000) // 最多处理1000个订单
        ).getContent();
    }
    
    /**
     * 创建订单更新请求
     */
    private com.postion.airlineorderbackend.dto.OrderUpdateRequest createUpdateRequest(OrderStatus status, String reason) {
        com.postion.airlineorderbackend.dto.OrderUpdateRequest request = 
            new com.postion.airlineorderbackend.dto.OrderUpdateRequest();
        request.setStatus(status);
        request.setReason(reason);
        return request;
    }
    
    /**
     * 生成每日订单报告
     */
    private void generateDailyOrderReport() {
        try {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            
            // 统计今日各状态订单数量
            for (OrderStatus status : OrderStatus.values()) {
                long count = orderService.findByMultipleConditions(
                    null, null, status, startOfDay, endOfDay,
                    org.springframework.data.domain.PageRequest.of(0, 1)
                ).getTotalElements();
                
                if (count > 0) {
                    log.info("今日订单统计 - {}: {}个", status.getDescription(), count);
                }
            }
            
        } catch (Exception e) {
            log.error("生成每日订单报告时发生异常", e);
        }
    }
    
    /**
     * 清理过期的锁记录
     */
    private void cleanupExpiredLocks() {
        // 这里可以添加清理过期锁记录的逻辑
        // 由于ShedLock会自动管理锁的生命周期，通常不需要手动清理
        log.debug("锁记录清理完成");
    }
    
    /**
     * 获取定时任务配置信息
     */
    public String getTaskConfiguration() {
        return String.format(
            "定时任务配置：\n" +
            "- 待支付订单超时时间：%d分钟\n" +
            "- 出票中订单超时时间：%d分钟\n" +
            "- 出票失败订单自动取消时间：%d小时",
            paymentTimeoutMinutes, ticketingTimeoutMinutes, ticketingFailedTimeoutHours
        );
    }
}
