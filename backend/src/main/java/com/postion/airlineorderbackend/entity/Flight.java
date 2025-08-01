package com.postion.airlineorderbackend.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    indexes = {
        @Index(name = "idx_route_time", columnList = "fromAirport,toAirport,departureTime")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String flightNumber; // 航班号，例如 "CA1234"

    @Column(nullable = false, length = 10)
    private String fromAirport;

    @Column(nullable = false, length = 10)
    private String toAirport;

    @Column(nullable = false)
    private OffsetDateTime departureTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}