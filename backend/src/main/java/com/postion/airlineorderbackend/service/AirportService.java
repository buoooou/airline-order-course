package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.AirportDto;
import com.postion.airlineorderbackend.mapper.AirportMapper;
import com.postion.airlineorderbackend.repo.AirportRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirportService {

    private AirportRepository airportRepository;
    private AirportMapper airportMapper;

    @Transactional(readOnly = true)
    public List<AirportDto> getAllAirports() {
        return airportRepository.findAll().stream()
                .map(airportMapper::toDto)
                .collect(Collectors.toList());
    }
}