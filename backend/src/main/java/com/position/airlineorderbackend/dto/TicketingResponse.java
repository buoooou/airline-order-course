package com.position.airlineorderbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketingResponse {
    private String ticketNumber;
    private String orderNumber;
    private String status; // SUCCESS, FAILED, PENDING
    private String message;
    private LocalDateTime ticketingTime;
    private String seatNumber;
    private String gateNumber;
    private String errorCode;
    private String errorMessage;
} 