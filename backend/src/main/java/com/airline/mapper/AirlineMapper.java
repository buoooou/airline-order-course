package com.airline.mapper;

import com.airline.dto.AirlineDto;
import com.airline.entity.Airline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AirlineMapper {

    AirlineDto toDto(Airline airline);

    List<AirlineDto> toDtoList(List<Airline> airlines);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Airline toEntity(AirlineDto airlineDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(AirlineDto airlineDto, @MappingTarget Airline airline);
}