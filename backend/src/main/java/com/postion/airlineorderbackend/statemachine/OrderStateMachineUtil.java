package com.postion.airlineorderbackend.statemachine;

import com.postion.airlineorderbackend.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单状态机工具类
 * 提供便捷的状态查询和转换方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStateMachineUtil {

    private final OrderStateMachineService stateMachineService;

    /**
     * 获取所有状态信息
     */
    public List<Map<String, String>> getAllStates() {
        return Arrays.stream(OrderState.values())
                .map(state -> Map.of(
                        "code", state.name(),
                        "description", state.getDescription(),
                        "detail", state.getDetail(),
                        "isTerminal", String.valueOf(state.isTerminal())
                ))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有事件信息
     */
    public List<Map<String, String>> getAllEvents() {
        return Arrays.stream(OrderEvent.values())
                .map(event -> Map.of(
                        "code", event.name(),
                        "description", event.getDescription(),
                        "detail", event.getDetail()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 获取订单状态详情
     */
    public Map<String, Object> getOrderStateDetail(Long orderId) {
        OrderState currentState = stateMachineService.getCurrentState(orderId);
        if (currentState == null) {
            return Map.of("error", "订单不存在或状态获取失败");
        }

        return Map.of(
                "orderId", orderId,
                "currentState", currentState.name(),
                "description", currentState.getDescription(),
                "detail", currentState.getDetail(),
                "isTerminal", currentState.isTerminal(),
                "canPay", currentState.canPay(),
                "canCancel", currentState.canCancel()
        );
    }

    /**
     * 获取订单允许的下一个状态
     */
    public List<Map<String, String>> getAllowedNextStates(Long orderId) {
        OrderState currentState = stateMachineService.getCurrentState(orderId);
        if (currentState == null) {
            return List.of();
        }

        return Arrays.stream(OrderState.values())
                .filter(state -> isValidStateToStateTransition(currentState, state))
                .map(state -> Map.of(
                        "code", state.name(),
                        "description", state.getDescription(),
                        "detail", state.getDetail()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 获取订单允许的触发事件
     */
    public List<Map<String, String>> getAllowedEvents(Long orderId) {
        return Arrays.stream(OrderEvent.values())
                .filter(event -> stateMachineService.canTriggerEvent(orderId, event))
                .map(event -> Map.of(
                        "code", event.name(),
                        "description", event.getDescription(),
                        "detail", event.getDetail()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 验证状态到状态的直接转换是否有效
     * 
     * 用于获取订单在当前状态下可以转换到的所有可能状态
     * 
     * @param from 源状态
     * @param to 目标状态
     * @return true 转换有效，false 转换无效
     */
    private boolean isValidStateToStateTransition(OrderState from, OrderState to) {
        // 基于状态机配置验证转换
        switch (from) {
            case PENDING_PAYMENT:
                return to == OrderState.PAID || to == OrderState.CANCELLED;
            case PAID:
                return to == OrderState.TICKETING_IN_PROGRESS || to == OrderState.CANCELLED;
            case TICKETING_IN_PROGRESS:
                return to == OrderState.TICKETED || to == OrderState.TICKETING_FAILED || to == OrderState.CANCELLED;
            case TICKETING_FAILED:
                return to == OrderState.TICKETING_IN_PROGRESS || to == OrderState.CANCELLED;
            default:
                return false;
        }
    }

    /**
     * 验证订单是否可以执行某个事件
     */
    public boolean canExecuteEvent(Long orderId, OrderEvent event) {
        return stateMachineService.canTriggerEvent(orderId, event);
    }

    /**
     * 获取订单状态转换历史（简化版）
     */
    public String getStateTransitionPath(Order order) {
        return String.format("订单[%s]状态路径: %s -> %s", 
                order.getOrderNumber(), 
                order.getCreationDate(), 
                order.getStatus());
    }

    /**
     * 获取状态机状态描述
     */
    public String getStateDescription(OrderState state) {
        return String.format("%s (%s)", state.getDescription(), state.getDetail());
    }

    /**
     * 获取事件描述
     */
    public String getEventDescription(OrderEvent event) {
        return String.format("%s (%s)", event.getDescription(), event.getDetail());
    }
}