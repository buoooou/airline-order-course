package com.postion.airlineorderbackend.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.AsyncOrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单定时任务：取消超时未支付
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderScheduledTask {

    private final OrderRepository orderRepository;
    private final AsyncOrderService asyncOrderService;

    /**
     * 每10分钟执行一次：关闭超时未支付的订单
     * 
     * @SchedulerLock参数说明：
     * - Cron 表达式的格式为：秒 分 时 日 月 周 年（年份可选，不写表示每年）
     * - name：锁名称（唯一标识任务）
     * - lockAtMostFor：最大持有锁时间（防止节点宕机导致锁永久持有）
     * - lockAtLeastFor：最小持有锁时间（防止任务执行过快导致锁提前释放）
     */
    @Scheduled(cron = "0 */10 * * * ?") // 每10分钟执行
    @SchedulerLock(name = "closeTimeoutUnpaidOrders", lockAtMostFor = "PT5M", // 锁最多持有5分钟
            lockAtLeastFor = "PT1M" // 锁最少持有1分钟
    )
    public void closeTimeoutUnpaidOrders() {
        log.info("开始执行【关闭超时未支付订单】任务，当前时间：{}", LocalDateTime.now());

        // 1. 查询30分钟前创建且未支付的订单
        // 数据库定义为datetime(6)，到毫秒，不需要截位到秒
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);
        log.info("查询时间参数：{}", timeoutTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        List<Order> timeoutOrders = orderRepository.findByStatusAndCreationDateBefore(
                OrderStatus.PENDING_PAYMENT, timeoutTime);

        // 2. 批量更新状态为取消
        if (!timeoutOrders.isEmpty()) {
            timeoutOrders.forEach(order -> {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                // 3. 异步发送通知和记录日志
                asyncOrderService.sendOrderStatusNotice(order);
                asyncOrderService.logOrderOperation(order, "超时未支付，系统自动取消");
            });
            log.info("完成【关闭超时未支付订单】任务，处理数量：{}", timeoutOrders.size());
        } else {
            log.info("【关闭超时未支付订单】任务：无符合条件的订单");
        }
    }

}