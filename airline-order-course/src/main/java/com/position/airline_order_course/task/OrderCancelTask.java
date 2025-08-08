package com.position.airline_order_course.task;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.position.airline_order_course.dto.OrderStatus;
import com.position.airline_order_course.model.Order;
import com.position.airline_order_course.repo.OrderRepository;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/*
 * ShedLock组件开启定时任务：取消未支付的订单
 */
@Component
public class OrderCancelTask {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelTask.class);

    @Autowired
    private OrderRepository orderRepository;

    // 每天凌晨2点执行，取消超过15分钟未支付的订单
    @Scheduled(cron = "0 0 2 * * ?")
    // 任务执行完后设置锁的最小持有时间1分钟，任务异常最大持有时间1小时
    @SchedulerLock(name = "cancelUnpaidOrders", lockAtMostFor = "PT1H", lockAtLeastFor = "PT1M")
    public void cancelUnpaidOrders() {
        log.info("【定时任务】开始检查并取消支付超时的订单");

        // 计算15分钟前的时间
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

        // 查询所有状态为PENDING且创建时间早于15分钟前的订单
        List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDate(
                OrderStatus.PENDING_PAYMENT,
                fifteenMinutesAgo);

        if (!unpaidOrders.isEmpty()) {
            log.info("将超时订单的状态更新为CANCELLED", unpaidOrders.size());

            // 更新订单状态为CANCELLED
            for (Order order : unpaidOrders) {
                order.setStatus(OrderStatus.CANCELLED);
                log.debug("已被标记为取消", order.getId(), order.getCreationDate());
            }

            // 保存更新后的订单
            orderRepository.saveAll(unpaidOrders);
        } else {
            log.info("未发现支付超时的订单");
        }
    }
}