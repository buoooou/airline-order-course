package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.model.Order;

public interface AsyncOrderService {
    /**
     * 异步发送订单状态变更通知
     */
    void sendOrderStatusNotice(Order order);

    /**
     * 异步记录订单操作日志
     */
    void logOrderOperation(Order order, String operation);
}