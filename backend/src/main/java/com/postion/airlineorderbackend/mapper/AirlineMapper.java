package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.AirlineDto;
import com.postion.airlineorderbackend.model.Airline;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AirlineMapper {
    AirlineMapper INSTANCE = Mappers.getMapper(AirlineMapper.class);

    AirlineDto toDto(Airline airline);
    Airline toEntity(AirlineDto airlineDto);
}