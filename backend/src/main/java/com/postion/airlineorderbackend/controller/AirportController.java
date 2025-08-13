package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.AirportDto;
import com.postion.airlineorderbackend.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @GetMapping
    public List<AirportDto> getAllAirports() {
        return airportService.getAllAirports();
    }
}