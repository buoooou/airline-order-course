package com.postion.airlineorderbackend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.entity.FlightInfo;

public interface FlightInfoRepository extends JpaRepository<FlightInfo, Long> {

}
