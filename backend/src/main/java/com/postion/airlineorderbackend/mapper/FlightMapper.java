package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.FlightDto;
import com.postion.airlineorderbackend.model.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    FlightMapper INSTANCE = Mappers.getMapper(FlightMapper.class);

    FlightDto toDto(Flight flight);
    Flight toEntity(FlightDto flightDto);
}