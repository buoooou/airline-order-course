package com.postion.airlineorderbackend.statemachine;

/**
 * 订单状态转换事件
 * 定义触发订单状态变化的所有可能事件
 */
public enum OrderEvent {
    // 用户相关事件
    PAY("支付订单", "用户完成订单支付"),
    CANCEL("取消订单", "用户主动取消订单"),
    
    // 系统相关事件
    PROCESS_TICKETING("开始出票", "系统开始处理出票请求"),
    TICKETING_SUCCESS("出票成功", "机票出票成功"),
    TICKETING_FAILURE("出票失败", "机票出票失败"),
    AUTO_CANCEL("自动取消", "超时自动取消订单"),
    
    // 异常处理事件
    REFUND("退款", "订单取消后的退款处理"),
    RETRY_TICKETING("重试出票", "出票失败后重试");

    private final String description;
    private final String detail;

    OrderEvent(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }

    public String getDescription() {
        return description;
    }

    public String getDetail() {
        return detail;
    }
}