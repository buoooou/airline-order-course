package com.postion.airlineorderbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;

@Mapper(componentModel = "spring", uses = {UserMapper.class}) // 声明依赖的嵌套映射器
public interface OrderMapper {

    // 获取 Mapper 实例的便捷方式
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // 将 Order (Entity) 映射到 OrderDto
    OrderDto toOrderDto(Order order);

    // 将 OrderDto 映射到 Order (Entity)
    Order orderDtoToOrder(OrderDto orderDto);

}