package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class UpdateOrderRequest {
	@NotBlank(message = "订单状态不能为空")
    private String status;
	 @DecimalMin(value = "0.0", inclusive = false, message = "金额必须大于0")
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private Long userId;

    // Getter 和 Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
