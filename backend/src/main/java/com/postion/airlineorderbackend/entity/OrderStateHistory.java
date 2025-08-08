package com.postion.airlineorderbackend.entity;

import java.time.LocalDateTime;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_state_history")
@Data
public class OrderStateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "order_number", length = 250, nullable = false)
    private String orderNumber;

    @Column(name = "from_state", length = 50, nullable = false)
    private String fromState;

    @Column(name = "to_state", length = 50, nullable = false)
    private String toState;

    @Column(name = "event", length = 50, nullable = false)
    private String event;

    @Column(name = "operator", length = 100)
    private String operator;

    @Column(name = "operator_role", length = 50)
    private String operatorRole;

    @Column(name = "success", nullable = false)
    private Boolean success;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "request_data", length = 2000)
    private String requestData;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}