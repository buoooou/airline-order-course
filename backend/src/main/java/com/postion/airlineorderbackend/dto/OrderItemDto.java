package com.postion.airlineorderbackend.dto;


import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long orderId;
    private Long flightId;
    private BigDecimal price;
    private Integer quantity;
    private Long passengerId;
}