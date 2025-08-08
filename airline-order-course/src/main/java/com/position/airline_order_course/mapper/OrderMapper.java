package com.position.airline_order_course.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.position.airline_order_course.dto.OrderDto;
import com.position.airline_order_course.model.Order;

/*
 * Order实体对象转Dto
 */
@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDto ToOrderDto(Order order);

    List<OrderDto> toOrderDtoList(List<Order> orders);

}
