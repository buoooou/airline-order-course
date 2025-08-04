package com.postion.airlineorderbackend.model;

import javax.persistence.*;

import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "flight_info")
public class FlightInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 10)
    private String flightNumber;

    @Column(length = 50)
    private String departureCity;

    @Column(length = 50)
    private String arrivalCity;

    @Column
    private Timestamp departureTime;
}