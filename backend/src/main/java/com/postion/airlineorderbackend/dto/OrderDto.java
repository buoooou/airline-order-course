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

    public static OrderDto fromEntity(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        dto.setCreationDate(order.getCreationDate());
        dto.setUserId(order.getUserId());
        dto.setFlightId(order.getFlightId());
        return dto;
    }
}