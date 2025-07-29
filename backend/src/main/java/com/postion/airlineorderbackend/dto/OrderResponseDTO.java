package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.model.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单响应DTO")
public class OrderResponseDTO {
    @Schema(description = "订单ID", example = "1")
    private Long id;
    
    @Schema(description = "订单号", example = "ORD-2024-001")
    private String orderNumber;
    
    @Schema(description = "订单状态", example = "PENDING_PAYMENT")
    private OrderStatus status;
    
    @Schema(description = "订单金额", example = "100.00")
    private BigDecimal amount;
    
    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime creationDate;
    
    // 用户信息（不包含敏感数据）
    @Schema(description = "用户信息")
    private UserInfoDTO user;
    
    @Data
    @Schema(description = "用户信息DTO")
    public static class UserInfoDTO {
        @Schema(description = "用户ID", example = "1")
        private Long id;
        
        @Schema(description = "用户名", example = "testuser")
        private String username;
        
        @Schema(description = "用户角色", example = "USER")
        private String role;
        // 不包含password字段
    }
} 