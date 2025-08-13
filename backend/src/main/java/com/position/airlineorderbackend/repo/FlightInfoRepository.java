package com.position.airlineorderbackend.repo;

import com.position.airlineorderbackend.model.FlightInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightInfoRepository extends JpaRepository<FlightInfo, Long> {
} 