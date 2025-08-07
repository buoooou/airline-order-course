package com.postion.airlineorderbackend.statemachine;

/**
 * 订单状态枚举
 * 定义订单生命周期中的所有可能状态
 */
public enum OrderState {
    PENDING_PAYMENT("待支付", "订单已创建，等待用户支付"),
    PAID("已支付", "用户已完成支付，等待出票"),
    TICKETING_IN_PROGRESS("出票中", "正在处理出票请求"),
    TICKETING_FAILED("出票失败", "出票过程中发生错误"),
    TICKETED("已出票", "机票已成功出票"),
    CANCELLED("已取消", "订单已被取消");

    private final String description;
    private final String detail;

    OrderState(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }

    public String getDescription() {
        return description;
    }

    public String getDetail() {
        return detail;
    }

    /**
     * 检查是否为终止状态
     */
    public boolean isTerminal() {
        return this == TICKETED || this == CANCELLED;
    }

    /**
     * 检查是否可以进行支付
     */
    public boolean canPay() {
        return this == PENDING_PAYMENT;
    }

    /**
     * 检查是否可以取消
     */
    public boolean canCancel() {
        return this == PENDING_PAYMENT || this == PAID || this == TICKETING_IN_PROGRESS || this == TICKETING_FAILED;
    }
}