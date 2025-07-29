package com.postion.airlineorderbackend.mapper;

import java.util.ArrayList;
import java.util.List;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.model.User;

public class UserMapper {

  public static UserDto user2dto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    return dto;
  }

  public static List<UserDto> list2dto(List<User> users) {
    if (users == null) {
      return null;
    }
    List<UserDto> dtos = new ArrayList<UserDto>();
    for (User user : users) {
      dtos.add(user2dto(user));
    }
    return dtos;
  }

}
