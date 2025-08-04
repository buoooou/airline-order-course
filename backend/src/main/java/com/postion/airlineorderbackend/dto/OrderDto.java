package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private Order.OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private Long userId;
    private Long flightId;

}