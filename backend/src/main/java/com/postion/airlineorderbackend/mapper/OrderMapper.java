package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDto toDto(Order order);

    List<OrderDto> toDtoList(List<Order> orders);

    Order toEntity(OrderDto orderDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    OrderDto.UserDto userToUserDto(User user);

    List<OrderDto.UserDto> usersToUserDtos(List<User> orders);
}