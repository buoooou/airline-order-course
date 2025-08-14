package com.airline.mapper;

import com.airline.dto.FlightDto;
import com.airline.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AirlineMapper.class, AirportMapper.class})
public interface FlightMapper {

    @Mapping(source = "airline.id", target = "airlineId")
    @Mapping(source = "departureAirport.id", target = "departureAirportId")
    @Mapping(source = "arrivalAirport.id", target = "arrivalAirportId")
    @Mapping(source = "airline", target = "airline")
    @Mapping(source = "departureAirport", target = "departureAirport")
    @Mapping(source = "arrivalAirport", target = "arrivalAirport")
    @Mapping(target = "duration", expression = "java(calculateDuration(flight.getDepartureTime(), flight.getArrivalTime()))")
    FlightDto toDto(Flight flight);

    List<FlightDto> toDtoList(List<Flight> flights);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "departureAirport", ignore = true)
    @Mapping(target = "arrivalAirport", ignore = true)
    Flight toEntity(FlightDto flightDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "departureAirport", ignore = true)
    @Mapping(target = "arrivalAirport", ignore = true)
    void updateEntityFromDto(FlightDto flightDto, @MappingTarget Flight flight);

    default Integer calculateDuration(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (departureTime == null || arrivalTime == null) {
            return null;
        }
        return (int) ChronoUnit.MINUTES.between(departureTime, arrivalTime);
    }
}