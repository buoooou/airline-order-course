package com.position.airline_order_course.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.position.airline_order_course.dto.TicketResponse;
import com.position.airline_order_course.service.MockTicketService;

/*
 * 出票服务层实现类
 */
@Service
public class MockTicketServiceImpl implements MockTicketService {

    private static final Logger logger = LoggerFactory.getLogger(MockTicketServiceImpl.class);

    // Mock人工待处理任务 （CopyOnWriteArrayList保证线程安全）
    private final List<String> pendingTasks = new CopyOnWriteArrayList<>();

    @Override
    public TicketResponse<String> issueTicket(String orderId) {
        logger.info("正在为订单 {} 出票...", orderId);

        // 模拟失败率20%
        boolean success = new Random().nextDouble() > 0.2;

        if (!success) {
            pendingTasks.add(orderId);
            logger.warn("订单 {} 出票失败，已加入人工处理", orderId);
            return TicketResponse.error(400, "订单" + orderId + " 出票失败，已加入人工处理");
        }

        logger.info("订单 {} 出票成功", orderId);
        return TicketResponse.success("出票成功，订单号：" + orderId);
    }

    @Override
    public TicketResponse<List<String>> getPendingTasks() {
        return TicketResponse.success(new ArrayList<>(pendingTasks));
    }

    @Override
    public TicketResponse<String> retryTicket(String orderId) {
        if (!pendingTasks.contains(orderId)) {
            return TicketResponse.error(400, "订单" + orderId + " 不在待处理中");
        }

        logger.info("人工重试出票：订单 {}", orderId);
        boolean success = new Random().nextDouble() > 0.3;

        if (success) {
            pendingTasks.remove(orderId);
            return TicketResponse.success("订单" + orderId + " 人工重试出票成功");
        } else {
            return TicketResponse.error(400, "订单" + orderId + " 人工重试失败");
        }
    }
}