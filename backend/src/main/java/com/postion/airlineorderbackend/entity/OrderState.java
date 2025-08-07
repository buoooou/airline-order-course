package com.postion.airlineorderbackend.entity;

public enum OrderState {
    PENDING_PAYMENT("待支付"),
    PAID("已支付"),
    TICKETING_IN_PROGRESS("出票中"),
    TICKETING_FAILED("出票失败"),
    TICKETED("已出票"),
    CANCELLED("已取消");

    private final String description;

    OrderState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isTerminalState(OrderState state) {
        return state == TICKETED || state == CANCELLED || state == TICKETING_FAILED;
    }

    public static boolean canPay(OrderState currentState) {
        return currentState == PENDING_PAYMENT;
    }

    public static boolean canCancel(OrderState currentState) {
        return currentState == PENDING_PAYMENT || currentState == PAID || currentState == TICKETING_IN_PROGRESS;
    }
}