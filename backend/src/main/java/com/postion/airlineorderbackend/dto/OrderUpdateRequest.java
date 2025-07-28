package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.enums.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 更新订单请求数据传输对象
 * 用于更新订单状态和信息时的请求参数
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {
    
    /**
     * 新的订单状态 - 必须提供
     */
    @NotNull(message = "订单状态不能为空")
    private OrderStatus status;
    
    /**
     * 状态更新原因 - 可选，用于记录状态变更的原因
     */
    @Size(max = 500, message = "状态更新原因长度不能超过500个字符")
    private String reason;
    
    /**
     * 备注信息 - 可选，用于补充说明
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;
    
    /**
     * 操作人员ID - 可选，用于记录是谁执行的操作
     */
    private Long operatorId;
    
    /**
     * 操作人员姓名 - 可选，用于记录操作人员信息
     */
    @Size(max = 100, message = "操作人员姓名长度不能超过100个字符")
    private String operatorName;
    
    /**
     * 是否强制更新 - 默认为false，如果为true则跳过某些验证
     */
    private Boolean forceUpdate = false;
    
    /**
     * 通知用户 - 默认为true，状态更新后是否通知用户
     */
    private Boolean notifyUser = true;
    
    /**
     * 构造函数 - 仅更新状态
     * @param status 新状态
     */
    public OrderUpdateRequest(OrderStatus status) {
        this.status = status;
    }
    
    /**
     * 构造函数 - 更新状态和原因
     * @param status 新状态
     * @param reason 更新原因
     */
    public OrderUpdateRequest(OrderStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }
    
    /**
     * 构造函数 - 更新状态、原因和备注
     * @param status 新状态
     * @param reason 更新原因
     * @param remarks 备注信息
     */
    public OrderUpdateRequest(OrderStatus status, String reason, String remarks) {
        this.status = status;
        this.reason = reason;
        this.remarks = remarks;
    }
    
    /**
     * 检查是否为取消操作
     * @return 是否为取消操作
     */
    public boolean isCancelOperation() {
        return this.status == OrderStatus.CANCELLED;
    }
    
    /**
     * 检查是否为支付操作
     * @return 是否为支付操作
     */
    public boolean isPaymentOperation() {
        return this.status == OrderStatus.PAID;
    }
    
    /**
     * 检查是否为出票操作
     * @return 是否为出票操作
     */
    public boolean isTicketingOperation() {
        return this.status == OrderStatus.TICKETING_IN_PROGRESS || 
               this.status == OrderStatus.TICKETED;
    }
    
    /**
     * 获取操作描述
     * @return 操作描述
     */
    public String getOperationDescription() {
        if (this.status == null) {
            return "未知操作";
        }
        
        switch (this.status) {
            case PAID:
                return "支付订单";
            case TICKETING_IN_PROGRESS:
                return "开始出票";
            case TICKETED:
                return "出票完成";
            case TICKETING_FAILED:
                return "出票失败";
            case CANCELLED:
                return "取消订单";
            default:
                return "更新订单状态为: " + this.status.getDescription();
        }
    }
    
    /**
     * 获取完整的操作日志信息
     * @return 操作日志信息
     */
    public String getOperationLog() {
        StringBuilder log = new StringBuilder();
        log.append(getOperationDescription());
        
        if (this.reason != null && !this.reason.trim().isEmpty()) {
            log.append(", 原因: ").append(this.reason);
        }
        
        if (this.operatorName != null && !this.operatorName.trim().isEmpty()) {
            log.append(", 操作人: ").append(this.operatorName);
        }
        
        if (this.remarks != null && !this.remarks.trim().isEmpty()) {
            log.append(", 备注: ").append(this.remarks);
        }
        
        return log.toString();
    }
    
    /**
     * 验证请求参数的有效性
     * @return 验证结果和错误信息
     */
    public String validateRequest() {
        if (this.status == null) {
            return "订单状态不能为空";
        }
        
        // 如果是取消操作，建议提供取消原因
        if (isCancelOperation() && (this.reason == null || this.reason.trim().isEmpty())) {
            return "取消订单时建议提供取消原因";
        }
        
        // 如果是出票失败，必须提供失败原因
        if (this.status == OrderStatus.TICKETING_FAILED && 
            (this.reason == null || this.reason.trim().isEmpty())) {
            return "出票失败时必须提供失败原因";
        }
        
        return null; // 验证通过
    }
}
