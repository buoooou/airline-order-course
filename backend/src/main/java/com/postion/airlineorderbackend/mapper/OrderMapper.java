package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import org.mapstruct.*;

/**
 * Order实体与DTO之间的映射接口
 * 使用MapStruct自动生成映射实现
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OrderMapper {

    /**
     * 将Order实体转换为OrderDto
     * @param order 订单实体
     * @return 订单DTO
     */
    OrderDto toDto(Order order);

    /**
     * 将OrderDto转换为Order实体
     * @param orderDto 订单DTO
     * @return 订单实体
     */
    @Mapping(target = "user", ignore = true) // 用户关系需要特殊处理
    Order toEntity(OrderDto orderDto);

    /**
     * 更新Order实体
     * @param orderDto 包含更新数据的DTO
     * @param order 要更新的实体
     * @return 更新后的实体
     */
    @Mapping(target = "id", ignore = true) // 不更新ID
    @Mapping(target = "user", ignore = true) // 用户关系需要特殊处理
    Order updateOrderFromDto(OrderDto orderDto, @MappingTarget Order order);
}