package com.postion.airlineorderbackend.exception;

/**
 * 非法订单状态流转异常
 */
public class IllegalOrderStateTransitionException extends RuntimeException {
    public IllegalOrderStateTransitionException(String message) {
        super(message);
    }
}