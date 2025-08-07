package com.postion.airlineorderbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;

@Mapper(componentModel = "spring") // Generates a Spring Bean for injection
public interface OrderMapper {
	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    // MapStruct can map nested objects automatically if they have a correspo

    @Mappings({
        @Mapping(source = "user", target = "user"), // Map the user obj
        @Mapping(target = "flightInfo", ignore = true) // flightInfo is
    })
    OrderDto toDto(Order order);

    // This method will be used by the above toDto method to map the User ent
    OrderDto.UserDto userToUserDto(User user);
}
