package com.postion.airlineorderbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {}