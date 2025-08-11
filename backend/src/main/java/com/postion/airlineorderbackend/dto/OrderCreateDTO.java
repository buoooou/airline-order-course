package com.postion.airlineorderbackend.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单创建请求DTO")
public class OrderCreateDTO {
    
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    @Schema(description = "订单金额", example = "100.00", minimum = "0.01")
    private BigDecimal amount;
    
    // 订单号通常由系统自动生成，所以这里不需要
    // 用户信息从当前登录用户获取，所以这里也不需要
    // 创建时间由系统自动设置，所以这里也不需要
} 