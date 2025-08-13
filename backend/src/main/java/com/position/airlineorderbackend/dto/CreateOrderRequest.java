package com.position.airlineorderbackend.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@Schema(description = "创建订单请求")
public class CreateOrderRequest {
    
    @Schema(description = "航班信息ID", example = "1")
    @NotNull(message = "航班信息ID不能为空")
    private Long flightInfoId;
    
    @Schema(description = "订单金额", example = "1500.00")
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    private BigDecimal amount;
    
    @Schema(description = "乘客姓名", example = "张三")
    @NotBlank(message = "乘客姓名不能为空")
    private String passengerName;
    
    @Schema(description = "乘客身份证号", example = "110101199001011234")
    @NotBlank(message = "乘客身份证号不能为空")
    private String passengerIdCard;
    
    @Schema(description = "联系电话", example = "13800138000")
    @NotBlank(message = "联系电话不能为空")
    private String phoneNumber;
    
    @Schema(description = "备注信息", example = "靠窗座位")
    private String remarks;
} 