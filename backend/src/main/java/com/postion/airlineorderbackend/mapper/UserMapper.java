package com.postion.airlineorderbackend.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mappings({
      @Mapping(source = "id", target = "userid"),
      @Mapping(source = "username", target = "username"),
      @Mapping(source = "role", target = "role")
  })
  public UserDto user2dto(User user);

  default public List<UserDto> list2dto(List<User> users) {
    if (users == null) {
      return null;
    }
    List<UserDto> dtos = new ArrayList<UserDto>();
    users.forEach(user -> dtos.add(user2dto(user)));
    return dtos;
  }

}
