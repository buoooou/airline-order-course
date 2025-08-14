package com.postion.airlineorderbackend.scheduler;

import com.postion.airlineorderbackend.entity.OrderStateHistory;
import com.postion.airlineorderbackend.repository.OrderStateHistoryRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单状态历史定时查询任务
 * 使用ShedLock分布式锁确保在集群环境中只有一个实例执行任务
 */
@Component
public class OrderStateHistoryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OrderStateHistoryScheduler.class);

    private final OrderStateHistoryRepository orderStateHistoryRepository;

    public OrderStateHistoryScheduler(OrderStateHistoryRepository orderStateHistoryRepository) {
        this.orderStateHistoryRepository = orderStateHistoryRepository;
    }

    /**
     * 每小时的5分、10分、15分...55分查询失败的订单状态转换记录
     * 使用ShedLock分布式锁确保任务在集群环境中只执行一次
     */
    // @Scheduled(cron = "0 5/5 * * * *") // 每小时的5分、10分、15分...55分执行
    @SchedulerLock(name = "queryFailedStateTransitions", lockAtLeastFor = "30s", lockAtMostFor = "50s")
    public void queryFailedStateTransitions() {
        try {
            logger.info("开始定时查询失败的订单状态转换记录 - 时间: {}", LocalDateTime.now());

            List<OrderStateHistory> failedTransitions = orderStateHistoryRepository
                    .findBySuccessOrderByCreatedAtDesc(false);

            if (failedTransitions.isEmpty()) {
                logger.info("当前没有失败的订单状态转换记录");
            } else {
                logger.warn("发现 {} 条失败的订单状态转换记录:", failedTransitions.size());
                failedTransitions.forEach(transition -> {
                    logger.warn("订单ID: {}, 从状态: {} -> 到状态: {}, 失败原因: {}, 创建时间: {}",
                            transition.getOrderId(),
                            transition.getFromState(),
                            transition.getToState(),
                            transition.getErrorMessage(),
                            transition.getCreatedAt());
                });
            }

            logger.info("定时查询任务完成 - 时间: {}", LocalDateTime.now());

        } catch (Exception e) {
            logger.error("执行定时查询任务时发生异常", e);
        }
    }

    // /**
    // * 每小时的整点分析失败的订单状态转换趋势
    // * 使用ShedLock分布式锁确保任务在集群环境中只执行一次
    // */
    // @Scheduled(cron = "0 0 * * * *") // 每小时的整点执行
    // @SchedulerLock(name = "analyzeFailedTransitions", lockAtLeastFor = "2m",
    // lockAtMostFor = "4m")
    // public void analyzeFailedTransitions() {
    // try {
    // logger.info("开始分析失败的订单状态转换趋势 - 时间: {}", LocalDateTime.now());

    // List<OrderStateHistory> recentFailedTransitions = orderStateHistoryRepository
    // .findBySuccessOrderByCreatedAtDesc(false);

    // if (recentFailedTransitions.isEmpty()) {
    // logger.info("最近没有失败的订单状态转换记录");
    // return;
    // }

    // // 按错误类型分组统计
    // java.util.Map<String, Long> errorTypeCount = recentFailedTransitions.stream()
    // .collect(java.util.stream.Collectors.groupingBy(
    // OrderStateHistory::getErrorMessage,
    // java.util.stream.Collectors.counting()));

    // logger.warn("最近失败的订单状态转换统计:");
    // errorTypeCount.forEach((errorMessage, count) -> {
    // logger.warn("错误类型: {}, 出现次数: {}", errorMessage, count);
    // });

    // // 按状态转换类型统计
    // java.util.Map<String, Long> stateTransitionCount =
    // recentFailedTransitions.stream()
    // .collect(java.util.stream.Collectors.groupingBy(
    // h -> h.getFromState() + " -> " + h.getToState(),
    // java.util.stream.Collectors.counting()));

    // logger.warn("状态转换失败统计:");
    // stateTransitionCount.forEach((transition, count) -> {
    // logger.warn("转换路径: {}, 失败次数: {}", transition, count);
    // });

    // logger.info("失败趋势分析完成 - 时间: {}", LocalDateTime.now());

    // } catch (Exception e) {
    // logger.error("执行失败趋势分析时发生异常", e);
    // }
    // }
}