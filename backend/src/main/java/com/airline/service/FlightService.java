package com.airline.service;

import com.airline.dto.FlightDto;
import com.airline.dto.FlightSearchDto;
import com.airline.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightService {

    FlightDto createFlight(FlightDto flightDto);

    Optional<FlightDto> getFlightById(Long id);

    Optional<FlightDto> getFlightByNumber(String flightNumber);

    Page<FlightDto> getAllFlights(Pageable pageable);

    Page<FlightDto> searchFlights(FlightSearchDto searchDto, Pageable pageable);

    Page<FlightDto> getFlightsByAirline(Long airlineId, Pageable pageable);

    Page<FlightDto> getFlightsByStatus(Flight.Status status, Pageable pageable);

    FlightDto updateFlight(Long id, FlightDto flightDto);

    void deleteFlight(Long id);

    FlightDto updateFlightStatus(Long id, Flight.Status status);

    void updateSeatAvailability(Long flightId, int seatChange);

    List<FlightDto> getFlightsBetweenDates(LocalDateTime startTime, LocalDateTime endTime);

    void updateFlightStatuses();

    long countFlightsByStatus(Flight.Status status);

    List<FlightDto> getAvailableFlights(String departureCode, String arrivalCode, 
                                       LocalDateTime startDate, LocalDateTime endDate);
}