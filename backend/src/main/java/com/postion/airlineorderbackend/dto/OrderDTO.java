package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.enums.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 * 用于前后端数据交互，包含订单的完整信息
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 航班信息ID
     */
    private Long flightInfoId;
    
    /**
     * 订单号
     */
    private String orderNumber;
    
    /**
     * 订单状态
     */
    private OrderStatus status;
    
    /**
     * 订单状态描述
     */
    private String statusDescription;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 创建时间
     */
    private LocalDateTime creationDate;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdated;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 出票时间
     */
    private LocalDateTime ticketingTime;
    
    /**
     * 取消时间
     */
    private LocalDateTime cancellationTime;
    
    /**
     * 取消原因
     */
    private String cancellationReason;
    
    /**
     * 备注信息
     */
    private String remarks;
    
    /**
     * 关联的用户信息
     */
    private UserDTO user;
    
    /**
     * 关联的航班信息
     */
    private FlightInfoDTO flightInfo;
    
    /**
     * 乘客数量
     */
    private Integer passengerCount;
    
    /**
     * 用户名（冗余字段，便于显示）
     */
    private String username;
    
    /**
     * 航班号（冗余字段，便于显示）
     */
    private String flightNumber;
    
    /**
     * 乘客姓名列表（逗号分隔）
     */
    private String passengerNames;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    private String contactEmail;
    
    /**
     * 航空公司
     */
    private String airline;
    
    /**
     * 出发机场
     */
    private String departureAirport;
    
    /**
     * 到达机场
     */
    private String arrivalAirport;
    
    /**
     * 出发时间
     */
    private LocalDateTime departureTime;
    
    /**
     * 到达时间
     */
    private LocalDateTime arrivalTime;
    
    /**
     * 出票开始时间
     */
    private LocalDateTime ticketingStartTime;
    
    /**
     * 出票完成时间
     */
    private LocalDateTime ticketingCompletionTime;
    
    /**
     * 出票失败原因
     */
    private String ticketingFailureReason;
    
    /**
     * 订单持续时间（分钟）
     */
    private Long durationMinutes;
    
    /**
     * 是否可以支付
     */
    private Boolean canPay;
    
    /**
     * 是否可以取消
     */
    private Boolean canCancel;
    
    /**
     * 是否已完成
     */
    private Boolean completed;
    
    /**
     * 是否已取消
     */
    private Boolean cancelled;
    
    /**
     * 构造函数 - 基本信息
     */
    public OrderDTO(Long id, String orderNumber, OrderStatus status, BigDecimal amount,
                   LocalDateTime creationDate, LocalDateTime lastUpdated) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.statusDescription = status != null ? status.getDescription() : "";
        this.amount = amount;
        this.creationDate = creationDate;
        this.lastUpdated = lastUpdated;
        
        // 计算衍生字段
        this.calculateDerivedFields();
    }
    
    /**
     * 计算衍生字段
     */
    private void calculateDerivedFields() {
        // 计算订单持续时间
        if (this.creationDate != null) {
            LocalDateTime endTime = this.lastUpdated != null ? this.lastUpdated : LocalDateTime.now();
            this.durationMinutes = java.time.Duration.between(this.creationDate, endTime).toMinutes();
        }
        
        // 计算订单状态相关的布尔值
        if (this.status != null) {
            this.canPay = this.status == OrderStatus.PENDING_PAYMENT;
            this.canCancel = this.status != OrderStatus.CANCELLED && this.status != OrderStatus.TICKETED;
            this.completed = this.status == OrderStatus.TICKETED;
            this.cancelled = this.status == OrderStatus.CANCELLED;
        } else {
            this.canPay = false;
            this.canCancel = false;
            this.completed = false;
            this.cancelled = false;
        }
    }
    
    /**
     * 获取订单摘要信息
     * @return 订单摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("订单号: ").append(this.orderNumber);
        summary.append(", 状态: ").append(this.statusDescription);
        summary.append(", 金额: ¥").append(this.amount);
        
        if (this.flightInfo != null) {
            summary.append(", 航班: ").append(this.flightInfo.getFlightNumber());
            summary.append(" (").append(this.flightInfo.getRouteDescription()).append(")");
        }
        
        return summary.toString();
    }
    
    /**
     * 获取订单状态的CSS样式类名
     * @return CSS样式类名
     */
    public String getStatusCssClass() {
        if (this.status == null) {
            return "status-unknown";
        }
        
        switch (this.status) {
            case PENDING_PAYMENT:
                return "status-pending";
            case PAID:
                return "status-paid";
            case TICKETING_IN_PROGRESS:
                return "status-processing";
            case TICKETED:
                return "status-success";
            case TICKETING_FAILED:
                return "status-failed";
            case CANCELLED:
                return "status-cancelled";
            default:
                return "status-unknown";
        }
    }
    
    /**
     * 获取订单状态的颜色代码
     * @return 颜色代码
     */
    public String getStatusColor() {
        if (this.status == null) {
            return "#666666";
        }
        
        switch (this.status) {
            case PENDING_PAYMENT:
                return "#faad14"; // 橙色
            case PAID:
                return "#1890ff"; // 蓝色
            case TICKETING_IN_PROGRESS:
                return "#722ed1"; // 紫色
            case TICKETED:
                return "#52c41a"; // 绿色
            case TICKETING_FAILED:
                return "#ff4d4f"; // 红色
            case CANCELLED:
                return "#8c8c8c"; // 灰色
            default:
                return "#666666";
        }
    }
    
    /**
     * 获取订单持续时间描述
     * @return 持续时间描述
     */
    public String getDurationDescription() {
        if (this.durationMinutes == null || this.durationMinutes <= 0) {
            return "刚刚创建";
        }
        
        if (this.durationMinutes < 60) {
            return this.durationMinutes + "分钟前";
        } else if (this.durationMinutes < 1440) { // 24小时
            long hours = this.durationMinutes / 60;
            return hours + "小时前";
        } else {
            long days = this.durationMinutes / 1440;
            return days + "天前";
        }
    }
    
    /**
     * 检查订单是否超时
     * @param timeoutMinutes 超时时间（分钟）
     * @return 是否超时
     */
    public boolean isTimeout(long timeoutMinutes) {
        if (this.status != OrderStatus.PENDING_PAYMENT || this.creationDate == null) {
            return false;
        }
        
        LocalDateTime timeoutTime = this.creationDate.plusMinutes(timeoutMinutes);
        return LocalDateTime.now().isAfter(timeoutTime);
    }
    
    /**
     * 获取下一个可能的状态列表
     * @return 下一个可能的状态描述
     */
    public String getNextPossibleActions() {
        if (this.status == null) {
            return "";
        }
        
        StringBuilder actions = new StringBuilder();
        
        switch (this.status) {
            case PENDING_PAYMENT:
                actions.append("支付, 取消");
                break;
            case PAID:
                actions.append("出票, 取消");
                break;
            case TICKETING_IN_PROGRESS:
                actions.append("等待出票完成, 取消");
                break;
            case TICKETING_FAILED:
                actions.append("重试出票, 取消");
                break;
            case TICKETED:
                actions.append("退票");
                break;
            case CANCELLED:
                actions.append("无可用操作");
                break;
        }
        
        return actions.toString();
    }
}
