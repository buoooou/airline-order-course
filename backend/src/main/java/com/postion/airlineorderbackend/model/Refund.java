package com.postion.airlineorderbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Data
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_order_id", nullable = false)
    private Long originalOrderId;

    @Column(name = "refund_type", nullable = false)
    private String refundType;

    @Column(nullable = false)
    private BigDecimal fee;

    @Column(nullable = false)
    private String status;

    private String reason;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
}