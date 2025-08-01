package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.entity.OrderStatus;

import lombok.Data;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private String flightNumber;
    private OrderStatus status;
    private String userEmail;
}
