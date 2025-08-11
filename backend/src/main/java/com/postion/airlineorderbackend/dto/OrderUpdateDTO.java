package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.model.OrderStatus;
import lombok.Data;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单更新请求DTO")
public class OrderUpdateDTO {
    
    @NotNull(message = "订单状态不能为空")
    @Schema(description = "订单状态", example = "PAID", required = true)
    private OrderStatus status;
    
    // 可以根据需要添加其他可更新的字段
    // 比如备注信息、支付方式等
    @Schema(description = "备注信息", example = "客户要求加急处理")
    private String remark;
} 