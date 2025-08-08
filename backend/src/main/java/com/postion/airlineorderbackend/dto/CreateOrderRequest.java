package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
	@NotBlank(message = "订单号不能为空")
    private String orderNumber;
	
	@NotBlank(message = "订单状态不能为空")
    private String status;
	
	@NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "金额必须大于0")
    private BigDecimal amount;
    private LocalDateTime creationDate;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;

}
