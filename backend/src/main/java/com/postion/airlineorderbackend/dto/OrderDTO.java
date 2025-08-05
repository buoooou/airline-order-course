package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.postion.airlineorderbackend.entity.Order;

public class OrderDTO {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private Long userId;

    // 构造器
    public OrderDTO() {}

    public OrderDTO(Long id, String orderNumber, String status, BigDecimal amount, LocalDateTime creationDate, Long userId) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.amount = amount;
        this.creationDate = creationDate;
        this.userId = userId;
    }
    

    public static OrderDTO fromEntity(Order order) {
        if (order == null) return null;

        return new OrderDTO(
            order.getId(),
            order.getOrderNumber(),
            order.getStatus(),
            order.getAmount(),
            order.getCreationDate(),
            order.getUser() != null ? order.getUser().getId() : null
        );
    }


    // Getter & Setter
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
