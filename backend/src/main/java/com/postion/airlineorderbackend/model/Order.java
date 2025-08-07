package com.postion.airlineorderbackend.model;

import javax.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 映射数据库中的订单表
 * 
 * @author Postion
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Entity
@Table(name = "orders_yb")
public class Order {
    /**
     * 订单唯一标识
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     * 唯一标识，格式：ORDER-YYYYMMDD-XXXXXX
     */
    @Column(unique = true, nullable = false)
    private String orderNumber;

    /**
     * 订单状态
     * 使用枚举类型存储，包括：待支付、已支付、出票中、出票失败、已出票、已取消
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * 订单金额
     * 精确到小数点后两位，不能为空
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 订单创建时间
     * 记录订单创建的时间戳
     */
    @Column(nullable = false)
    private LocalDateTime creationDate;

    /**
     * 票号
     * 出票成功后生成的票号
     */
    @Column
    private String ticketNumber;

    /**
     * 关联的用户
     * 多对一关系，关联到用户表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

