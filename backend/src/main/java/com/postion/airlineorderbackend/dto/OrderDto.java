package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.postion.airlineorderbackend.model.OrderStatus;

import lombok.Data;

@Data
public class OrderDto {

    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime creationDate;

    private UserDto user;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentTime;

    @Data
    public static class UserDto{
        private Long id;
        private String username;
    }
}
