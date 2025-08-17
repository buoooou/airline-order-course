package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Generates a Spring Bean for injection
public interface OrderMapper {

    @Mapping(target = "flightInfo", ignore = true)
    OrderDto toDto(Order order);

    // This method will be used by the above toDto method to map the User entity to
    // UserDto
    OrderDto.UserDto userToUserDto(User user);
}