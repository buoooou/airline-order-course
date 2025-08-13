package com.postion.airlineorderbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlightDto {
    private Long id;
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String airlineCode;
    private String status;
}