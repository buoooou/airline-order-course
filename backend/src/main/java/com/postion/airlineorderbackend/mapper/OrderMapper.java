package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDto orderToOrderDto(Order order);

    List<OrderDto> ordersToOrderDtos(List<Order> orders);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    OrderDto.UserDto userToUserDto(User user);

    List<OrderDto.UserDto> usersToUserDtos(List<User> orders);
}