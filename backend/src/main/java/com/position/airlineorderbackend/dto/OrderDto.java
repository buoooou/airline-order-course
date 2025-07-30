package com.position.airlineorderbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.position.airlineorderbackend.model.OrderStatus;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime createdDate;
    private Long userId;
    private Long flightInfoId;
} 