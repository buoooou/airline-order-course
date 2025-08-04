package com.postion.airlineorderbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('PENDING_PAYMENT', 'PAID', 'TICKETING_IN_PROGRESS', 'TICKETING_FAILED', 'TICKETED', 'CANCELLED')")
    private OrderStatus status;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;
    
    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "departure_city")
    private String departureCity;

    @Column(name = "arrival_city")
    private String arrivalCity;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;
    
    public enum OrderStatus {
        PENDING_PAYMENT,    // 待支付
        PAID,               // 已支付
        TICKETING_IN_PROGRESS, // 出票中
        TICKETING_FAILED,   // 出票失败
        TICKETED,           // 已出票
        CANCELLED           // 已取消
    }
    
    // 状态机转换方法
    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this.status) {
            case PENDING_PAYMENT:
                return newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
            case PAID:
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS || newStatus == OrderStatus.CANCELLED;
            case TICKETING_IN_PROGRESS:
                return newStatus == OrderStatus.TICKETED || newStatus == OrderStatus.TICKETING_FAILED;
            case TICKETING_FAILED:
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS || newStatus == OrderStatus.CANCELLED;
            case TICKETED:
                return false; // 最终状态，不能转换
            case CANCELLED:
                return false; // 最终状态，不能转换
            default:
                return false;
        }
    }
    
    public void transitionTo(OrderStatus newStatus) {
        if (canTransitionTo(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException("Cannot transition from " + this.status + " to " + newStatus);
        }
    }
} 