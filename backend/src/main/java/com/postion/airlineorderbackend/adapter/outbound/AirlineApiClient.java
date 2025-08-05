package com.postion.airlineorderbackend.adapter.outbound;

import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AirlineApiClient {

    public String issueTicket(Long orderId) {
        log.info("开始处理出票请求，订单ID: {}", orderId);
        try {
            // 模拟网络延迟（2-5秒）
            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));

            // 模拟接口成功率（80%成功，20%失败）
            if (ThreadLocalRandom.current().nextInt(10) < 8) {
                String ticketNumber = "TKT" + System.currentTimeMillis();
                log.info("出票成功，订单ID: {}, 票号: {}", orderId, ticketNumber);
                return ticketNumber;
            } else {
                String errorMessage = "出票失败，航司返回错误";
                log.error("出票失败，订单ID: {}, 原因: {}", orderId, errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMessage = "出票中断";
            log.error("出票中断，订单ID: {}, 原因: {}", orderId, errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public boolean cancelTicket(String ticketNumber) {
        log.info("开始处理取消订单请求，票号: {}", ticketNumber);
        try {
            // 模拟网络延迟（2-5秒）
            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));

            // 模拟接口成功率（90%成功，10%失败）
            if (ThreadLocalRandom.current().nextInt(10) < 9) {
                log.info("取消订单成功，票号: {}", ticketNumber);
                return true;
            } else {
                String errorMessage = "取消订单失败，航司返回错误";
                log.error("取消订单失败，票号: {}, 原因: {}", ticketNumber, errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMessage = "取消订单中断";
            log.error("取消订单中断，票号: {}, 原因: {}", ticketNumber, errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

}
