package com.postion.airlineorderbackend.statemachine;


import java.util.Map;

import com.postion.airlineorderbackend.exception.IllegalOrderStateTransitionException;

/**
 * 订单状态机
 */
public class OrderStateMachine {
    // 订单状态枚举
    // 状态流转规则
    private static final Map<String, String[]> TRANSITION_RULES = new java.util.HashMap<>();

    static {
        // 待支付可以流转到已支付或已取消
        TRANSITION_RULES.put("PENDING_PAYMENT", new String[] { "PAID", "CANCELLED" });
        // 已支付可以流转到已出票或已取消
        TRANSITION_RULES.put("PAID", new String[] { "ISSUED", "CANCELLED" });
        // 已出票可以流转到已完成
        TRANSITION_RULES.put("ISSUED", new String[] { "COMPLETED" });
        // 已取消和已完成是终止状态，不能再流转
        TRANSITION_RULES.put("CANCELLED", new String[] {});
        TRANSITION_RULES.put("COMPLETED", new String[] {});
    }

    /**
     * 检查状态流转是否合法
     * 
     * @param currentState 当前状态
     * @param newState     目标状态
     * @throws IllegalOrderStateTransitionException 如果流转不合法
     */
    public static void validateTransition(String currentState, String newState) {
        for (String allowedState : TRANSITION_RULES.get(currentState)) {
            if (allowedState.equals(newState)) {
                return;
            }
        }
        throw new IllegalOrderStateTransitionException(
                String.format("非法状态流转: 从 %s 到 %s", currentState, newState));
    }
}