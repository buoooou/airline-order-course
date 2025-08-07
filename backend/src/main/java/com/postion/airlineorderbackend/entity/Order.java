package com.postion.airlineorderbackend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", length = 250, nullable = false)
    private String orderNumber;

    @Column(name = "status", nullable = false)
    private String status; // 改为String类型以兼容状态机

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "flight_id", nullable = false)
    private Long flightId;

    // 可选：添加与AppUser的关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private AppUser appUser;

    // 可选：添加与Flight的关联关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", insertable = false, updatable = false)
    private FlightInfo flightInfo;



}
