package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.FlightDto;
import com.postion.airlineorderbackend.mapper.FlightMapper;
import com.postion.airlineorderbackend.repo.FlightRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private FlightRepository flightRepository;
    private FlightMapper flightMapper;

    @Transactional(readOnly = true)
    public List<FlightDto> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(flightMapper::toDto)
                .collect(Collectors.toList());
    }
}