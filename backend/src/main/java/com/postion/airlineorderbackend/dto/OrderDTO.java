package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.postion.airlineorderbackend.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private UserDto user;
    private Map<String, Object> flightInfo;

    public static class UserDto {
        private Long id;
        private String username;
    }
}
