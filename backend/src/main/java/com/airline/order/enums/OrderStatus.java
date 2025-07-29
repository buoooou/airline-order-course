package com.airline.order.enums;

/**
 * 订单状态枚举
 * 定义了订单在整个生命周期中的各种状态
 */
public enum OrderStatus {
    PENDING_PAYMENT,      // 待支付
    PAID,                 // 已支付
    TICKETING_IN_PROGRESS,// 出票中
    TICKETING_FAILED,     // 出票失败
    TICKETED,             // 已出票
    CANCELLED             // 已取消
}