package com.airline.mapper;

import com.airline.dto.PassengerDto;
import com.airline.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PassengerMapper {

    @Mapping(source = "user.id", target = "userId")
    PassengerDto toDto(Passenger passenger);

    List<PassengerDto> toDtoList(List<Passenger> passengers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Passenger toEntity(PassengerDto passengerDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(PassengerDto passengerDto, @MappingTarget Passenger passenger);
}