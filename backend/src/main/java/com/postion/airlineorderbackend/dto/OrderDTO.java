package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.enums.OrderStatus;

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
    private LocalDateTime creationDate;
    private Long userId;   

    public static OrderDTO fromEntity(Order order) {
        if (order == null) return null;

        return new OrderDTO(
            order.getId(),
            order.getOrderNumber(),
            order.getStatus(),
            order.getAmount(),
            order.getCreationDate(),
            order.getUser() != null ? order.getUser().getId() : null
        );
    }
}
