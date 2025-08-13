package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private UserDto user;
    private Map<String, Object> flightInfo;
}
