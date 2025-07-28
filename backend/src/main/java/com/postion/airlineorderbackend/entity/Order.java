package com.postion.airlineorderbackend.entity;

import com.postion.airlineorderbackend.enums.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应数据库表 orders_qiaozhe
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Entity
@Table(name = "orders_qiaozhe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    /**
     * 订单ID - 主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 订单号 - 唯一标识，不能为空
     */
    @Column(name = "order_number", nullable = false, unique = true, length = 255)
    private String orderNumber;
    
    /**
     * 订单状态 - 枚举类型，不能为空
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
    
    /**
     * 订单金额 - 不能为空，精度为19位，小数点后2位
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    /**
     * 创建时间 - 不能为空
     */
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;
    
    /**
     * 关联的用户 - 多对一关系
     * 多个订单属于一个用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orders_user_new"))
    private User user;
    
    /**
     * 关联的航班信息 - 一对一关系
     * 一个订单对应一个航班信息
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_info_id", foreignKey = @ForeignKey(name = "fk_orders_flight_info"))
    private FlightInfo flightInfo;
    
    /**
     * 乘客数量 - 默认为1
     */
    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount = 1;
    
    /**
     * 乘客姓名列表 - 存储为JSON字符串
     */
    @Column(name = "passenger_names", length = 1000)
    private String passengerNames;
    
    /**
     * 联系电话 - 必须提供
     */
    @Column(name = "contact_phone", nullable = false, length = 11)
    private String contactPhone;
    
    /**
     * 联系邮箱 - 可选
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;
    
    /**
     * 出票开始时间
     */
    @Column(name = "ticketing_start_time")
    private LocalDateTime ticketingStartTime;
    
    /**
     * 出票完成时间
     */
    @Column(name = "ticketing_completion_time")
    private LocalDateTime ticketingCompletionTime;
    
    /**
     * 出票失败原因
     */
    @Column(name = "ticketing_failure_reason", length = 500)
    private String ticketingFailureReason;
    
    /**
     * 最后更新时间 - 自动更新
     */
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    /**
     * 支付时间 - 订单支付完成时设置
     */
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;
    
    /**
     * 出票时间 - 订单出票完成时设置
     */
    @Column(name = "ticketing_time")
    private LocalDateTime ticketingTime;
    
    /**
     * 取消时间 - 订单取消时设置
     */
    @Column(name = "cancellation_time")
    private LocalDateTime cancellationTime;
    
    /**
     * 取消原因 - 订单取消时的原因说明
     */
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    
    /**
     * 备注信息 - 订单的额外说明
     */
    @Column(name = "remarks", length = 1000)
    private String remarks;
    
    /**
     * 在持久化之前设置创建时间和最后更新时间
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.creationDate == null) {
            this.creationDate = now;
        }
        this.lastUpdated = now;
    }
    
    /**
     * 在更新之前设置最后更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * 构造函数 - 用于创建新订单
     * @param orderNumber 订单号
     * @param amount 订单金额
     * @param user 关联用户
     * @param flightInfo 关联航班信息
     */
    public Order(String orderNumber, BigDecimal amount, User user, FlightInfo flightInfo) {
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.user = user;
        this.flightInfo = flightInfo;
        this.status = OrderStatus.PENDING_PAYMENT;
        this.creationDate = LocalDateTime.now();
    }
    
    /**
     * 更新订单状态
     * 包含状态机验证逻辑
     * 
     * @param newStatus 新状态
     * @throws IllegalStateException 如果状态转换不合法
     */
    public void updateStatus(OrderStatus newStatus) {
        if (this.status == null) {
            throw new IllegalStateException("当前订单状态为空，无法更新状态");
        }
        
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("订单状态不能从 %s 转换为 %s", 
                    this.status.getDescription(), newStatus.getDescription())
            );
        }
        
        OrderStatus oldStatus = this.status;
        this.status = newStatus;
        
        // 根据新状态设置相应的时间戳
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case PAID:
                this.paymentTime = now;
                break;
            case TICKETED:
                this.ticketingTime = now;
                break;
            case CANCELLED:
                this.cancellationTime = now;
                break;
        }
        
        this.lastUpdated = now;
    }
    
    /**
     * 取消订单
     * @param reason 取消原因
     */
    public void cancel(String reason) {
        this.updateStatus(OrderStatus.CANCELLED);
        this.cancellationReason = reason;
        
        // 如果有关联的航班信息，释放座位
        if (this.flightInfo != null) {
            this.flightInfo.releaseSeats(1); // 假设每个订单预订1个座位
        }
    }
    
    /**
     * 检查订单是否可以支付
     * @return 是否可以支付
     */
    public boolean canPay() {
        return this.status == OrderStatus.PENDING_PAYMENT;
    }
    
    /**
     * 检查订单是否可以取消
     * @return 是否可以取消
     */
    public boolean canCancel() {
        return this.status != OrderStatus.CANCELLED && this.status != OrderStatus.TICKETED;
    }
    
    /**
     * 检查订单是否已完成
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return this.status == OrderStatus.TICKETED;
    }
    
    /**
     * 检查订单是否已取消
     * @return 是否已取消
     */
    public boolean isCancelled() {
        return this.status == OrderStatus.CANCELLED;
    }
    
    /**
     * 检查订单是否超时（用于自动取消）
     * @param timeoutMinutes 超时时间（分钟）
     * @return 是否超时
     */
    public boolean isTimeout(long timeoutMinutes) {
        if (this.status != OrderStatus.PENDING_PAYMENT) {
            return false;
        }
        
        LocalDateTime timeoutTime = this.creationDate.plusMinutes(timeoutMinutes);
        return LocalDateTime.now().isAfter(timeoutTime);
    }
    
    /**
     * 获取订单持续时间（分钟）
     * @return 订单持续时间
     */
    public long getDurationMinutes() {
        LocalDateTime endTime = this.lastUpdated != null ? this.lastUpdated : LocalDateTime.now();
        return java.time.Duration.between(this.creationDate, endTime).toMinutes();
    }
    
    /**
     * 生成订单摘要信息
     * @return 订单摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("订单号: ").append(this.orderNumber);
        summary.append(", 状态: ").append(this.status.getDescription());
        summary.append(", 金额: ¥").append(this.amount);
        
        if (this.flightInfo != null) {
            summary.append(", 航班: ").append(this.flightInfo.getFlightNumber());
            summary.append(" (").append(this.flightInfo.getDepartureAirport())
                   .append("→").append(this.flightInfo.getArrivalAirport()).append(")");
        }
        
        return summary.toString();
    }
}
