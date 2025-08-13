package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.PassengerDto;
import com.postion.airlineorderbackend.model.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);

    PassengerDto toDto(Passenger passenger);
    Passenger toEntity(PassengerDto passengerDto);
}