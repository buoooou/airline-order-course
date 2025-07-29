package com.postion.airlineorderbackend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.mapper.UserMapper;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.UserService;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Override
  public List<UserDto> getAllUsers() {
    List<User> result = userRepository.findAll();
    return UserMapper.list2dto(result);
  }

  @Override
  public UserDto findByUserId(Long id) {
    Optional<User> result = userRepository.findById(id);
    return result.isPresent() ? UserMapper.user2dto(result.get()) : null;
  };

  @Override
  public List<UserDto> findByUsername(String username) {
    List<User> result = userRepository.findByUsername(username);
    return UserMapper.list2dto(result);
  }
}
