package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.AirlineDto;
import com.postion.airlineorderbackend.service.AirlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airlines")
public class AirlineController {

    @Autowired
    private AirlineService airlineService;

    @GetMapping
    public List<AirlineDto> getAllAirlines() {
        return airlineService.getAllAirlines();
    }
}