package com.postion.airlineorderbackend.model;

import java.time.LocalDateTime;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "flight_info_ycr")
@Data
public class FlightInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "departure_city", length = 250, nullable = false)
    private String departureCity;

    @Column(name = "arrival_city", length = 250, nullable = false)
    private String arrivalCity;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private Order order;
}
