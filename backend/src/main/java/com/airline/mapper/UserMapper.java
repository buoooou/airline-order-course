package com.airline.mapper;

import com.airline.dto.UserDto;
import com.airline.dto.UserRegistrationDto;
import com.airline.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "status", constant = "ACTIVE")
    User fromRegistrationDto(UserRegistrationDto registrationDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UserDto userDto, @MappingTarget User user);
}