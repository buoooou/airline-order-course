package com.position.airlineorderbackend.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.position.airlineorderbackend.model.OrderStatus;

@Data
@Schema(description = "订单数据传输对象")
public class OrderDto {
    @Schema(description = "订单ID", example = "1")
    private Long id;
    
    @Schema(description = "订单号", example = "ORD-2024-001")
    private String orderNumber;
    
    @Schema(description = "订单状态", example = "PENDING_PAYMENT")
    private OrderStatus status;
    
    @Schema(description = "订单金额", example = "1500.00")
    private BigDecimal amount;
    
    @Schema(description = "创建时间", example = "2024-01-15T10:30:00")
    private LocalDateTime createdDate;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "航班信息ID", example = "1")
    private Long flightInfoId;
} 