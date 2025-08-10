package com.postion.airlineorderbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import com.postion.airlineorderbackend.model.OrderStatus;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private UserDto user;
    private Map<String, Object> flightInfo; // 航班相关信息收集
}