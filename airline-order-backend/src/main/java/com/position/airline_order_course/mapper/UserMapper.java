package com.position.airline_order_course.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.position.airline_order_course.dto.UserDto;
import com.position.airline_order_course.model.User;

/*
 * User实体对象转Dto
 */
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);
}
