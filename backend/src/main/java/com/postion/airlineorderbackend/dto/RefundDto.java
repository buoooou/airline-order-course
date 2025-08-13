package com.postion.airlineorderbackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RefundDto {
    private Long id;
    private Long originalOrderId;
    private String refundType;
    private BigDecimal fee;
    private String status;
    private String reason;
}