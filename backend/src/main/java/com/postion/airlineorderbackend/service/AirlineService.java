package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.AirlineDto;
import com.postion.airlineorderbackend.mapper.AirlineMapper;
import com.postion.airlineorderbackend.repo.AirlineRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private AirlineRepository airlineRepository;
    private AirlineMapper airlineMapper;

    @Transactional(readOnly = true)
    public List<AirlineDto> getAllAirlines() {
        return airlineRepository.findAll().stream()
                .map(airlineMapper::toDto)
                .collect(Collectors.toList());
    }
}