package com.postion.airlineorderbackend.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // 防止与 SQL 的关键字 "order" 冲突
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal amount;

    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ===== 无参构造函数 (JPA 必须要有) =====
    public Order() {
    }

    // ===== 有参构造函数 =====
    public Order(Long id, String orderNumber, OrderStatus status, BigDecimal amount, LocalDateTime creationDate, User user) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.amount = amount;
        this.creationDate = creationDate;
        this.user = user;
    }

    // ===== Getter & Setter =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ===== toString() 方法 (调试用) =====
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", status=" + status +
                ", amount=" + amount +
                ", creationDate=" + creationDate +
                ", user=" + (user != null ? user.getUsername() : "null") +
                '}';
    }
}