package com.position.airlineorderbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "flight_info")
@Data
public class FlightInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String departureTime;
} 