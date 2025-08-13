package com.postion.airlineorderbackend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal amount;
    @Column(name = "creation_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;
    @Column(name = "update_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime updateDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentTime;

}
