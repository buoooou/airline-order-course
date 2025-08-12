package com.postion.airlineorderbackend.adapter.outbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AirlineAppClient {

    public void issueTicket(String orderId) {
        log.info("模拟向航空公司发送出票请求，订单号：{}", orderId);

        try {
            Thread.sleep(3000); // 模拟延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("出票请求被中断", e);
        }

        if (Math.random() < 0.2) {
            throw new RuntimeException("模拟出票失败：航空公司系统异常");
        }

        log.info("出票成功：{}", orderId);
    }
}
