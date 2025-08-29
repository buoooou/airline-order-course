package com.postion.airlineorderbackend.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateOrderRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    // 可以根据需要添加其他字段，如航班信息等

    // 构造函数
    public CreateOrderRequest() {}

    public CreateOrderRequest(BigDecimal amount) {
        this.amount = amount;
    }

    // Getter和Setter
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}