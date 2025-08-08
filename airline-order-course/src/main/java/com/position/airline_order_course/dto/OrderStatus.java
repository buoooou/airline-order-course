package com.position.airline_order_course.dto;

/*
 * 订单状态（枚举类型定义）
 */
public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    TICKETING_IN_PROGRESS,
    TICKETING_FAILED,
    TICKETED,
    CANCELLED
}
