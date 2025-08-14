package com.postion.airlineorderbackend.converter;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface Converter {
    Converter INSTANCE = Mappers.getMapper(Converter.class);

    List<OrderDto> toResOrderDtoList(List<Order> orderList);
    OrderDto toResOrderDto(Order order);

    List<UserDto> toResUserDtoList(List<User> userList);
    UserDto toResUserDto(User user);
}
