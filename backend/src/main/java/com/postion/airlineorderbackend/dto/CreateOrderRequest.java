package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;

public record CreateOrderRequest(
        Long flightId,
        BigDecimal amount
) {}