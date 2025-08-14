package com.airline.mapper;

import com.airline.dto.AirportDto;
import com.airline.entity.Airport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AirportMapper {

    AirportDto toDto(Airport airport);

    List<AirportDto> toDtoList(List<Airport> airports);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Airport toEntity(AirportDto airportDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(AirportDto airportDto, @MappingTarget Airport airport);
}