package com.position.airlineorderbackend.scheduler;

import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.model.OrderStatus;
import com.position.airlineorderbackend.repo.OrderRepository;
import com.position.airlineorderbackend.service.OrderService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OrderScheduler.class);

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    // 配置参数，可以通过application.yml配置
    @Value("${order.timeout.minutes:30}")
    private int orderTimeoutMinutes;
    
    @Value("${ticketing.retry.max-attempts:3}")
    private int maxRetryAttempts;

    /**
     * 定时检查超时订单
     * 使用ShedLock确保分布式环境下只有一个实例执行
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    @SchedulerLock(
        name = "checkTimeoutOrders",
        lockAtLeastFor = "30s",
        lockAtMostFor = "5m"
    )
    @Transactional
    public void checkTimeoutOrders() {
        logger.info("开始检查超时订单...");
        
        try {
            LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(orderTimeoutMinutes);
            List<Order> timeoutOrders = orderRepository.findTimeoutOrders(OrderStatus.PENDING_PAYMENT, timeoutTime);
            
            if (timeoutOrders.isEmpty()) {
                logger.info("没有发现超时订单");
                return;
            }
            
            logger.info("发现 {} 个超时订单，开始处理...", timeoutOrders.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (Order order : timeoutOrders) {
                try {
                    String result = orderService.cancelOrder(order.getId());
                    logger.info("订单 {} 超时取消成功: {}", order.getOrderNumber(), result);
                    successCount++;
                } catch (Exception e) {
                    logger.error("订单 {} 超时取消失败: {}", order.getOrderNumber(), e.getMessage(), e);
                    failCount++;
                }
            }
            
            logger.info("超时订单处理完成 - 成功: {}, 失败: {}", successCount, failCount);
            
        } catch (Exception e) {
            logger.error("检查超时订单时发生错误", e);
        }
    }

    /**
     * 定时处理出票失败的重试
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    @SchedulerLock(
        name = "retryFailedTicketing",
        lockAtLeastFor = "1m",
        lockAtMostFor = "10m"
    )
    @Transactional
    public void retryFailedTicketing() {
        logger.info("开始重试出票失败的订单...");
        
        try {
            List<Order> failedOrders = orderRepository.findOrdersByStatus(OrderStatus.TICKETING_FAILED);
            
            if (failedOrders.isEmpty()) {
                logger.info("没有发现出票失败的订单");
                return;
            }
            
            logger.info("发现 {} 个出票失败的订单，开始重试...", failedOrders.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (Order order : failedOrders) {
                try {
                    // 重新开始出票流程
                    String result = orderService.retryTicketing(order.getId());
                    logger.info("订单 {} 出票重试成功: {}", order.getOrderNumber(), result);
                    successCount++;
                    
                    // 添加延迟避免过于频繁的请求
                    Thread.sleep(1000);
                    
                } catch (Exception e) {
                    logger.error("订单 {} 出票重试失败: {}", order.getOrderNumber(), e.getMessage(), e);
                    failCount++;
                }
            }
            
            logger.info("出票失败重试处理完成 - 成功: {}, 失败: {}", successCount, failCount);
            
        } catch (Exception e) {
            logger.error("重试出票失败订单时发生错误", e);
        }
    }

    /**
     * 定时清理过期的锁记录
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    @SchedulerLock(
        name = "cleanupExpiredLocks",
        lockAtLeastFor = "5m",
        lockAtMostFor = "30m"
    )
    public void cleanupExpiredLocks() {
        logger.info("开始清理过期的锁记录...");
        
        try {
            // ShedLock会自动清理过期的锁记录，这里主要是记录日志
            // 如果需要手动清理，可以执行SQL: DELETE FROM shedlock WHERE lock_until < NOW()
            
            logger.info("过期锁记录清理完成");
        } catch (Exception e) {
            logger.error("清理过期锁记录时发生错误", e);
        }
    }
    
    /**
     * 定时统计订单状态
     */
    @Scheduled(cron = "0 */30 * * * ?") // 每30分钟执行一次
    @SchedulerLock(
        name = "orderStatusStatistics",
        lockAtLeastFor = "1m",
        lockAtMostFor = "5m"
    )
    public void orderStatusStatistics() {
        logger.info("开始统计订单状态...");
        
        try {
            List<Object[]> statistics = orderRepository.countOrdersByStatus();
            
            logger.info("订单状态统计结果:");
            for (Object[] stat : statistics) {
                OrderStatus status = (OrderStatus) stat[0];
                Long count = (Long) stat[1];
                logger.info("  {}: {} 个订单", status, count);
            }
            
        } catch (Exception e) {
            logger.error("统计订单状态时发生错误", e);
        }
    }
    
    /**
     * 定时检查长时间处于出票中的订单
     */
    @Scheduled(fixedRate = 180000) // 每3分钟执行一次
    @SchedulerLock(
        name = "checkStuckTicketingOrders",
        lockAtLeastFor = "30s",
        lockAtMostFor = "3m"
    )
    @Transactional
    public void checkStuckTicketingOrders() {
        logger.info("开始检查长时间处于出票中的订单...");
        
        try {
            // 检查超过10分钟仍处于出票中的订单
            LocalDateTime stuckTime = LocalDateTime.now().minusMinutes(10);
            List<Order> stuckOrders = orderRepository.findTimeoutOrders(OrderStatus.TICKETING_IN_PROGRESS, stuckTime);
            
            if (stuckOrders.isEmpty()) {
                logger.info("没有发现长时间处于出票中的订单");
                return;
            }
            
            logger.info("发现 {} 个长时间处于出票中的订单", stuckOrders.size());
            
            for (Order order : stuckOrders) {
                logger.warn("订单 {} 已处于出票中状态超过10分钟，可能需要人工干预", order.getOrderNumber());
                // 这里可以添加告警通知逻辑
            }
            
        } catch (Exception e) {
            logger.error("检查长时间处于出票中的订单时发生错误", e);
        }
    }
} 