package com.postion.airlineorderbackend.dto;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TicketDto {
    private String ticketNumber;
    private Long orderItemId;
    private Long passengerId;
    private Long flightId;
    private String seatNumber;
    private String status;
    private LocalDateTime issueDate;
}