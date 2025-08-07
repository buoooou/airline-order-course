package com.postion.airlineorderbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private Long userId;
    private Long flightId;

}