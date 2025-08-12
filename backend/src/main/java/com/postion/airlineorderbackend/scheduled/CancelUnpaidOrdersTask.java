package com.postion.airlineorderbackend.scheduled;

import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelUnpaidOrdersTask {

    private final OrderService orderService;

    @Scheduled(cron = "0 0 2 * * *") 
    @SchedulerLock(name = "CancelUnpaidOrdersTask", lockAtMostFor = "55s", lockAtLeastFor = "10s")
    public void cancelUnpaidOrders() {
        log.info("开始执行未支付订单取消任务...");
        orderService.cancelUnpaidOrders();
        log.info("未支付订单取消任务执行完毕。");
    }
}
