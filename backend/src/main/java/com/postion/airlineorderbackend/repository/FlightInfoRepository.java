package com.postion.airlineorderbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.entity.FlightInfo;

public interface FlightInfoRepository extends JpaRepository<FlightInfo, Long> {

}
