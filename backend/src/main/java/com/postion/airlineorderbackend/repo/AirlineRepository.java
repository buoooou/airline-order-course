package com.postion.airlineorderbackend.repo;

import com.postion.airlineorderbackend.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, String> {
}