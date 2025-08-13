package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.PassengerDto;
import com.postion.airlineorderbackend.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @GetMapping
    public List<PassengerDto> getAllPassengers() {
        return passengerService.getAllPassengers();
    }
}