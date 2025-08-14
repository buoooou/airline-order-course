package com.postion.airlineorderbackend.model;

public enum OrderStatus {
    PENDING_PAYMENT,       // 待支付
    PAID,                  // 已支付
    TICKETING_IN_PROGRESS, // 出票中
    TICKETING_FAILED,      // 出票失败
    TICKETED,              // 已出票（完成）
    CANCELLED;             // 已取消
    
    // 定义一个抽象方法，强制每个状态都必须实现自己的转换逻辑
    public boolean canTransitionTo(OrderStatus nextStatus) {
    	switch (this) {
    		case PENDING_PAYMENT:
    			return nextStatus == PAID || nextStatus == CANCELLED;
    		case PAID:
    			return nextStatus == TICKETING_IN_PROGRESS || nextStatus == CANCELLED;
    		case TICKETING_IN_PROGRESS:
    			return nextStatus == TICKETED || nextStatus == TICKETING_FAILED;
    		case TICKETING_FAILED:
    			return nextStatus == CANCELLED || nextStatus == TICKETING_IN_PROGRESS;
    		default:
    			return false; // 默认不允许转换
    	}
    }
}
