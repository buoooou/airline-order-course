package com.airline.mapper;

import com.airline.dto.OrderDto;
import com.airline.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    List<OrderDto> toDtoList(List<Order> orders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderDto orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    void updateEntityFromDto(OrderDto orderDto, @MappingTarget Order order);
}