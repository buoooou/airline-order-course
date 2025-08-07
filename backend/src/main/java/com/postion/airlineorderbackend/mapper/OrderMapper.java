package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // MapStruct can map nested objects automatically if they have a corresponding mapping method
    @Mappings({
            // 如果有需要映射的特定字段，可以取消注释并修改
            // @Mapping(source = "user", target = "userDto"),
            @Mapping(target = "flightInfo", ignore = true)
    })
    OrderDto toDto(Order order);

    // This method will be used by the above toDto method to map the User entity to UserDto
    OrderDto.UserDto userToUserDto(User user);
}