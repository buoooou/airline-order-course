package com.postion.airlineorderbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.model.User;

@Mapper(componentModel = "spring") 
public interface UserMapper {

    // 获取 Mapper 实例的便捷方式
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // 将 User (Entity) 映射到 UserDTO
    @Mapping(source = "username", target = "userName")
    UserDto userToUserDto(User user);

    // 将 UserDto 映射到 User (Entity)
    @Mapping(source = "userName", target = "username")
    User userDtoToUser(UserDto userDto);
}
