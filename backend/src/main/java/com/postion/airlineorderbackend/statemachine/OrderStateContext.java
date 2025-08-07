package com.postion.airlineorderbackend.statemachine;

import com.postion.airlineorderbackend.entity.Order;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态机上下文
 * 存储状态转换过程中的业务数据和上下文信息
 */
@Data
public class OrderStateContext {
    
    private Long orderId;
    private Long userId;
    private String orderNumber;
    private OrderState currentState;
    private OrderState previousState;
    private OrderEvent triggeredEvent;
    private LocalDateTime stateChangeTime;
    private String operator;
    private String remark;
    
    // 扩展属性存储
    private Map<String, Object> extendedAttributes = new HashMap<>();
    
    // 业务相关数据
    private String paymentTransactionId;
    private String ticketNumber;
    private String failureReason;
    private Integer retryCount = 0;
    
    public OrderStateContext() {
        this.stateChangeTime = LocalDateTime.now();
    }
    
    public OrderStateContext(Order order) {
        this();
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.orderNumber = order.getOrderNumber();
        this.currentState = convertToOrderState(order.getStatus());
    }
    
    /**
     * 将实体状态转换为状态机状态
     */
    private OrderState convertToOrderState(String status) {
        return OrderState.valueOf(status);
    }
    
    /**
     * 将状态机状态转换为实体状态
     */
    public String convertToEntityStatus(OrderState state) {
        return state.name();
    }
    
    /**
     * 添加扩展属性
     */
    public void addAttribute(String key, Object value) {
        extendedAttributes.put(key, value);
    }
    
    /**
     * 获取扩展属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) extendedAttributes.get(key);
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    /**
     * 创建状态转换日志
     */
    public String createStateTransitionLog() {
        return String.format("订单[%s]状态转换: %s -> %s, 事件: %s, 操作人: %s, 时间: %s",
                orderNumber, previousState, currentState, triggeredEvent, operator, stateChangeTime);
    }
}