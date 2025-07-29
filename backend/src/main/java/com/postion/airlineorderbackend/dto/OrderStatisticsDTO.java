package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.model.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class OrderStatisticsDTO {
    private Long totalOrders;
    private BigDecimal totalAmount;
    private Map<OrderStatus, Long> ordersByStatus;
    private Long pendingPaymentCount;
    private Long paidCount;
    private Long ticketedCount;
    private Long cancelledCount;
} 