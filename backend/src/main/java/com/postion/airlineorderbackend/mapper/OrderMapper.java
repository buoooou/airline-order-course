package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring") // Generates a Spring Bean for injection
public interface OrderMapper {

    // Mapping the Order entity to OrderDto with additional fields handling
    @Mappings({
            @Mapping(source = "user", target = "user"), // Map the user object
            @Mapping(target = "flightInfo", ignore = true) // Add flightInfo separately, not mapped from entity
    })
    OrderDto toDto(Order order);

    // Mapping User entity to UserDto
    OrderDto.UserDto userToUserDto(User user);
}
