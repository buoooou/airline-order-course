package com.postion.airlineorderbackend.enums;

/**
 * 订单状态枚举
 * 定义订单在整个生命周期中的各种状态
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
public enum OrderStatus {
    
    /**
     * 待支付 - 订单已创建，等待用户支付
     */
    PENDING_PAYMENT("待支付"),
    
    /**
     * 已支付 - 用户已完成支付，等待出票
     */
    PAID("已支付"),
    
    /**
     * 出票中 - 正在进行出票处理
     */
    TICKETING_IN_PROGRESS("出票中"),
    
    /**
     * 出票失败 - 出票过程中发生错误
     */
    TICKETING_FAILED("出票失败"),
    
    /**
     * 已出票 - 出票成功，订单完成
     */
    TICKETED("已出票"),
    
    /**
     * 已取消 - 订单被取消
     */
    CANCELLED("已取消");
    
    private final String description;
    
    /**
     * 构造函数
     * @param description 状态描述
     */
    OrderStatus(String description) {
        this.description = description;
    }
    
    /**
     * 获取状态描述
     * @return 状态的中文描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否可以从当前状态转换到目标状态
     * 实现订单状态机的状态转换规则
     * 
     * @param targetStatus 目标状态
     * @return 是否可以转换
     */
    public boolean canTransitionTo(OrderStatus targetStatus) {
        switch (this) {
            case PENDING_PAYMENT:
                // 待支付状态可以转换为：已支付、已取消
                return targetStatus == PAID || targetStatus == CANCELLED;
                
            case PAID:
                // 已支付状态可以转换为：出票中、已取消
                return targetStatus == TICKETING_IN_PROGRESS || targetStatus == CANCELLED;
                
            case TICKETING_IN_PROGRESS:
                // 出票中状态可以转换为：已出票、出票失败、已取消
                return targetStatus == TICKETED || targetStatus == TICKETING_FAILED || targetStatus == CANCELLED;
                
            case TICKETING_FAILED:
                // 出票失败状态可以转换为：出票中（重试）、已取消
                return targetStatus == TICKETING_IN_PROGRESS || targetStatus == CANCELLED;
                
            case TICKETED:
                // 已出票状态只能转换为：已取消（退票）
                return targetStatus == CANCELLED;
                
            case CANCELLED:
                // 已取消状态是终态，不能转换到其他状态
                return false;
                
            default:
                return false;
        }
    }
    
    /**
     * 检查当前状态是否为终态
     * @return 是否为终态
     */
    public boolean isFinalStatus() {
        return this == TICKETED || this == CANCELLED;
    }
    
    /**
     * 检查当前状态是否为成功状态
     * @return 是否为成功状态
     */
    public boolean isSuccessStatus() {
        return this == TICKETED;
    }
    
    /**
     * 检查当前状态是否为失败状态
     * @return 是否为失败状态
     */
    public boolean isFailureStatus() {
        return this == CANCELLED || this == TICKETING_FAILED;
    }
}
