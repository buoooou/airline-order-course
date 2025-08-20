package com.postion.airlineorderbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.model.User;

@Mapper(componentModel="spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // @Mapping(source="User", target="UserDTO")
    UserDTO userToUserDTO(User user);

    // @Mapping(source="UserDTO", target="User")
    User userDTOToUser(UserDTO userDto);
}
