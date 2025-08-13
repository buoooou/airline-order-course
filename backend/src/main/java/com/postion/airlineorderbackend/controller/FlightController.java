package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.FlightDto;
import com.postion.airlineorderbackend.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
    public List<FlightDto> getAllFlights() {
        return flightService.getAllFlights();
    }
}