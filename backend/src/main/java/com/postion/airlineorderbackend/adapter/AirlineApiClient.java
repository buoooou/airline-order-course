package com.postion.airlineorderbackend.adapter;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.postion.airlineorderbackend.exception.AirlineApiException;

/**
 * 模拟航司API客户端，实现 AirlineApi 接口
 */
public class AirlineApiClient implements AirlineApi {

    // 使用SLF4J作为日志门面，可以在运行时切换不同的日志实现
    private static final Logger logger = LoggerFactory.getLogger(AirlineApiClient.class);

    // 模拟API调用成功率（例如80%）
    private static final int SUCCESS_RATE = 80;

    // 模拟网络延迟和处理时间范围（秒）
    private static final int MIN_DELAY = 2;
    private static final int MAX_DELAY = 6;

    @Override
    public String issueTicket(Long orderId) throws InterruptedException, AirlineApiException {
        logger.info("开始为订单 {} 调用航司接口出票...", orderId);

        // 1. 模拟网络延迟和处理时间
        int delay = ThreadLocalRandom.current().nextInt(MIN_DELAY, MAX_DELAY);
        try {
            TimeUnit.SECONDS.sleep(delay);
        } catch (InterruptedException e) {
            // 收到中断信号时，重新抛出异常，让上层处理
            Thread.currentThread().interrupt();
            throw e;
        }

        // 2. 模拟接口调用成功或失败
        if (ThreadLocalRandom.current().nextInt(100) < SUCCESS_RATE) {
            // 模拟成功
            String ticketNumber = "TKT-" + orderId + "-" + System.currentTimeMillis();
            logger.info("订单 {} 出票成功！票号: {}", orderId, ticketNumber);
            return ticketNumber;
        } else {
            // 模拟失败，抛出自定义异常
            String errorMessage = "航司返回错误: Insufficient seats";
            logger.error("订单 {} 出票失败: {}", orderId, errorMessage);
            throw new AirlineApiException(errorMessage);
        }
    }
}