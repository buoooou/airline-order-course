package com.postion.airlineorderbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "Order", target = "OrderDTO")
    OrderDTO orderToOrderDTO(Order order);

    OrderDTO.UserDto toUserDto(User user);

    @Mapping(source = "OrderDTO", target = "Order")
    Order orderDTOToOrder(OrderDTO orderDto);
}
