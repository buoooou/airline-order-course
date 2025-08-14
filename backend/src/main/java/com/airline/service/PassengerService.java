package com.airline.service;

import com.airline.dto.PassengerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PassengerService {

    PassengerDto createPassenger(PassengerDto passengerDto, Long userId);

    Optional<PassengerDto> getPassengerById(Long id);

    List<PassengerDto> getPassengersByUser(Long userId);

    Page<PassengerDto> getAllPassengers(Pageable pageable);

    Page<PassengerDto> searchPassengers(String keyword, Pageable pageable);

    PassengerDto updatePassenger(Long id, PassengerDto passengerDto);

    void deletePassenger(Long id);

    Optional<PassengerDto> getPassengerByPassport(String passportNumber);

    Optional<PassengerDto> getPassengerByIdCard(String idCardNumber);

    boolean existsByPassportNumber(String passportNumber);

    boolean existsByIdCardNumber(String idCardNumber);
}