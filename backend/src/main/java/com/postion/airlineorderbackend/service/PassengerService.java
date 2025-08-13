package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.PassengerDto;
import com.postion.airlineorderbackend.mapper.PassengerMapper;
import com.postion.airlineorderbackend.repo.PassengerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private PassengerRepository passengerRepository;
    private PassengerMapper passengerMapper;

    @Transactional(readOnly = true)
    public List<PassengerDto> getAllPassengers() {
        return passengerRepository.findAll().stream()
                .map(passengerMapper::toDto)
                .collect(Collectors.toList());
    }
}