package com.postion.airlineorderbackend.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.service.AsyncOrderService;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AsyncOrderServiceImpl implements AsyncOrderService {

    /**
     * 异步发送通知（指定使用asyncExecutor线程池）
     */
    @Async("asyncExecutor") // 关联自定义线程池
    @Override
    public void sendOrderStatusNotice(Order order) {
        log.info("开始异步发送订单[{}]状态通知，当前线程：{}",
                order.getOrderNumber(), Thread.currentThread().getName());

        // 模拟通知发送耗时（如调用短信/邮件接口）
        try {
            Thread.sleep(2000); // 模拟网络延迟
            log.info("订单[{}]状态通知发送成功，状态：{}",
                    order.getOrderNumber(), order.getStatus());
        } catch (InterruptedException e) {
            log.error("订单通知发送失败", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 异步记录日志
     */
    @Async("asyncExecutor")
    @Override
    public void logOrderOperation(Order order, String operation) {
        log.info("异步记录订单[{}]操作日志：{}，时间：{}",
                order.getOrderNumber(), operation, LocalDateTime.now());
        // 实际业务中可写入日志表或日志系统
    }
}