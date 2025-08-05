package com.position.airlineorderbackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TicketingRequest {
    private String orderNumber;
    private String flightNumber;
    private String passengerName;
    private String passengerId;
    private String seatClass;
    private BigDecimal amount;
    private String departureCity;
    private String arrivalCity;
    private String departureTime;
    private String airlineCode;
} 