package com.position.airline_order_course.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private String amount;
    private LocalDateTime creationDate;
    private UserDto user;

}