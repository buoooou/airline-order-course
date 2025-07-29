package com.postion.airlineorderbackend.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单状态枚举")
public enum OrderStatus {
    @Schema(description = "待支付")
    PENDING_PAYMENT,
    
    @Schema(description = "已支付")
    PAID,
    
    @Schema(description = "出票中")
    TICKETING_IN_PROGRESS,
    
    @Schema(description = "出票失败")
    TICKETING_FAILED,
    
    @Schema(description = "已出票")
    TICKETED,
    
    @Schema(description = "已取消")
    CANCELLED
}
