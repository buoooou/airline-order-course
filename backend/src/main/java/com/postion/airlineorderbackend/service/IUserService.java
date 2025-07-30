package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface IUserService {

    UserDto getUserByUsername(String username);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);
}