package com.airline.service.impl;

import com.airline.dto.FlightDto;
import com.airline.dto.FlightSearchDto;
import com.airline.entity.Airline;
import com.airline.entity.Airport;
import com.airline.entity.Flight;
import com.airline.exception.ResourceNotFoundException;
import com.airline.exception.ValidationException;
import com.airline.mapper.FlightMapper;
import com.airline.repository.AirlineRepository;
import com.airline.repository.AirportRepository;
import com.airline.repository.FlightRepository;
import com.airline.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final FlightMapper flightMapper;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository,
                            AirlineRepository airlineRepository,
                            AirportRepository airportRepository,
                            FlightMapper flightMapper) {
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.flightMapper = flightMapper;
    }

    @Override
    public FlightDto createFlight(FlightDto flightDto) {
        validateFlightDto(flightDto);
        
        Flight flight = flightMapper.toEntity(flightDto);
        
        Airline airline = airlineRepository.findById(flightDto.getAirlineId())
                .orElseThrow(() -> new ResourceNotFoundException("航空公司不存在"));
        
        Airport departureAirport = airportRepository.findById(flightDto.getDepartureAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("出发机场不存在"));
        
        Airport arrivalAirport = airportRepository.findById(flightDto.getArrivalAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("到达机场不存在"));
        
        flight.setAirline(airline);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setAvailableSeats(flight.getTotalSeats());
        
        Flight savedFlight = flightRepository.save(flight);
        return flightMapper.toDto(savedFlight);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlightDto> getFlightById(Long id) {
        return flightRepository.findById(id)
                .map(flightMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlightDto> getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .map(flightMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightDto> getAllFlights(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(flightMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightDto> searchFlights(FlightSearchDto searchDto, Pageable pageable) {
        Airport departureAirport = airportRepository.findByCode(searchDto.getDepartureAirportCode())
                .orElseThrow(() -> new ResourceNotFoundException("出发机场不存在"));
        
        Airport arrivalAirport = airportRepository.findByCode(searchDto.getArrivalAirportCode())
                .orElseThrow(() -> new ResourceNotFoundException("到达机场不存在"));
        
        LocalDateTime departureDate = searchDto.getDepartureDate().atStartOfDay();
        
        return flightRepository.searchFlights(
                departureAirport.getId(),
                arrivalAirport.getId(),
                departureDate,
                pageable
        ).map(flightMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightDto> getFlightsByAirline(Long airlineId, Pageable pageable) {
        if (!airlineRepository.existsById(airlineId)) {
            throw new ResourceNotFoundException("航空公司不存在");
        }
        return flightRepository.findByAirlineId(airlineId, pageable)
                .map(flightMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightDto> getFlightsByStatus(Flight.Status status, Pageable pageable) {
        return flightRepository.findByStatus(status, pageable)
                .map(flightMapper::toDto);
    }

    @Override
    public FlightDto updateFlight(Long id, FlightDto flightDto) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("航班不存在"));
        
        validateFlightDto(flightDto);
        
        if (flightDto.getAirlineId() != null && !flightDto.getAirlineId().equals(flight.getAirline().getId())) {
            Airline airline = airlineRepository.findById(flightDto.getAirlineId())
                    .orElseThrow(() -> new ResourceNotFoundException("航空公司不存在"));
            flight.setAirline(airline);
        }
        
        if (flightDto.getDepartureAirportId() != null && 
            !flightDto.getDepartureAirportId().equals(flight.getDepartureAirport().getId())) {
            Airport departureAirport = airportRepository.findById(flightDto.getDepartureAirportId())
                    .orElseThrow(() -> new ResourceNotFoundException("出发机场不存在"));
            flight.setDepartureAirport(departureAirport);
        }
        
        if (flightDto.getArrivalAirportId() != null && 
            !flightDto.getArrivalAirportId().equals(flight.getArrivalAirport().getId())) {
            Airport arrivalAirport = airportRepository.findById(flightDto.getArrivalAirportId())
                    .orElseThrow(() -> new ResourceNotFoundException("到达机场不存在"));
            flight.setArrivalAirport(arrivalAirport);
        }
        
        flightMapper.updateEntityFromDto(flightDto, flight);
        
        Flight savedFlight = flightRepository.save(flight);
        return flightMapper.toDto(savedFlight);
    }

    @Override
    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new ResourceNotFoundException("航班不存在");
        }
        flightRepository.deleteById(id);
    }

    @Override
    public FlightDto updateFlightStatus(Long id, Flight.Status status) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("航班不存在"));
        
        flight.setStatus(status);
        Flight savedFlight = flightRepository.save(flight);
        return flightMapper.toDto(savedFlight);
    }

    @Override
    public void updateSeatAvailability(Long flightId, int seatChange) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("航班不存在"));
        
        int newAvailableSeats = flight.getAvailableSeats() + seatChange;
        if (newAvailableSeats < 0 || newAvailableSeats > flight.getTotalSeats()) {
            throw new ValidationException("座位数量无效");
        }
        
        flight.setAvailableSeats(newAvailableSeats);
        flightRepository.save(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDto> getFlightsBetweenDates(LocalDateTime startTime, LocalDateTime endTime) {
        return flightMapper.toDtoList(
                flightRepository.findByDepartureTimeBetween(startTime, endTime)
        );
    }

    @Override
    public void updateFlightStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Flight> flightsToUpdate = flightRepository.findFlightsToUpdateStatus(now);
        
        for (Flight flight : flightsToUpdate) {
            if (flight.getDepartureTime().isBefore(now.minusHours(1))) {
                flight.setStatus(Flight.Status.DEPARTED);
            } else if (flight.getDepartureTime().isBefore(now.plusMinutes(30))) {
                flight.setStatus(Flight.Status.BOARDING);
            }
        }
        
        flightRepository.saveAll(flightsToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFlightsByStatus(Flight.Status status) {
        return flightRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDto> getAvailableFlights(String departureCode, String arrivalCode, 
                                              LocalDateTime startDate, LocalDateTime endDate) {
        return flightMapper.toDtoList(
                flightRepository.findAvailableFlights(departureCode, arrivalCode, startDate, endDate)
        );
    }

    private void validateFlightDto(FlightDto flightDto) {
        if (flightDto.getDepartureTime() != null && flightDto.getArrivalTime() != null) {
            if (flightDto.getDepartureTime().isAfter(flightDto.getArrivalTime())) {
                throw new ValidationException("出发时间不能晚于到达时间");
            }
        }
        
        if (flightDto.getDepartureAirportId() != null && flightDto.getArrivalAirportId() != null) {
            if (flightDto.getDepartureAirportId().equals(flightDto.getArrivalAirportId())) {
                throw new ValidationException("出发机场和到达机场不能相同");
            }
        }
    }
}