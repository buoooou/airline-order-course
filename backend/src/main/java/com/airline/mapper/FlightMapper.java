package com.airline.mapper;

import com.airline.dto.FlightDto;
import com.airline.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AirlineMapper.class, AirportMapper.class})
public interface FlightMapper {

    @Mapping(source = "airline.id", target = "airlineId")
    @Mapping(source = "departureAirport.id", target = "departureAirportId")
    @Mapping(source = "arrivalAirport.id", target = "arrivalAirportId")
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
}