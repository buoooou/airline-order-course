package com.postion.airlineorderbackend.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderDetailDto {
    // orders表的order_number
    private String orderNumber;
    // flights表的flight_number
    private String flightNumber;
    // flights表的departure_airport
    private String departureAirport;
    // flights表的arrival_airport
    private String arrivalAirport;
    // flights表的departure_time
    private String departureTime;
    // flights表的arrival_time
    private String arrivalTime;
    // tickets表的seat_number
    private String seatNumber;
    // passengers表的name
    private String passengerName;
    // passengers表的id_type
    private String passengerIdType;
    // passengers表的id_number
    private String passengerId;
    // passengers表的phone
    private String passengerPhone;
    // orders的price
    private String paymentAmount;
    // orders的payment_method
    private String paymentMethod;
    // orders的payment_status
    private String paymentStatus;
    // orders的payment_time
    private LocalDateTime paymentTime;
    // orders表的creation_date
    private LocalDateTime date;
    // orders表的status
    private String status;
    // orders表的update_date
    private LocalDateTime updatedAt;
}
