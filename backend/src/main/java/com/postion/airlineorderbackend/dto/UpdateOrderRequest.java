package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateOrderRequest {
	@NotBlank(message = "订单状态不能为空")
    private String status;
	 @DecimalMin(value = "0.0", inclusive = false, message = "金额必须大于0")
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private Long userId;
}
