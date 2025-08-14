package com.airline.mapper;

import com.airline.dto.OrderItemDto;
import com.airline.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FlightMapper.class, PassengerMapper.class})
public interface OrderItemMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "flight.id", target = "flightId")
    @Mapping(source = "passenger.id", target = "passengerId")
    OrderItemDto toDto(OrderItem orderItem);

    List<OrderItemDto> toDtoList(List<OrderItem> orderItems);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "flight", ignore = true)
    @Mapping(target = "passenger", ignore = true)
    OrderItem toEntity(OrderItemDto orderItemDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "flight", ignore = true)
    @Mapping(target = "passenger", ignore = true)
    void updateEntityFromDto(OrderItemDto orderItemDto, @MappingTarget OrderItem orderItem);
}