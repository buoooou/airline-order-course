package com.position.airlineorderbackend.mapper;

import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.position.airlineorderbackend.config.MapStructConfig;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface OrderMapper {

    /**
     * 将Order实体转换为OrderDto
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "flightInfo.id", target = "flightInfoId")
    OrderDto toDto(Order order);

    /**
     * 将OrderDto转换为Order实体
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "flightInfo", ignore = true)
    Order toEntity(OrderDto orderDto);

    /**
     * 更新Order实体（忽略null值）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateEntity(@MappingTarget Order order, OrderDto orderDto);

    /**
     * 将Order列表转换为OrderDto列表
     */
    List<OrderDto> toDtoList(List<Order> orders);

    /**
     * 将OrderDto列表转换为Order列表
     */
    List<Order> toEntityList(List<OrderDto> orderDtos);
} 