package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.AirportDto;
import com.postion.airlineorderbackend.model.Airport;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AirportMapper {
    AirportMapper INSTANCE = Mappers.getMapper(AirportMapper.class);

    AirportDto toDto(Airport airport);
    Airport toEntity(AirportDto airportDto);
}