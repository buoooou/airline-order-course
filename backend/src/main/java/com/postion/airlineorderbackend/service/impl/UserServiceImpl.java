package com.postion.airlineorderbackend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.mapper.UserMapper;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  @Override
  public List<UserDto> getAllUsers() {
    List<User> result = userRepository.findAll();
    return userMapper.list2dto(result);
  }

  @Override
  public UserDto findByUserId(Long id) throws DataNotFoundException {
    Optional<User> result = userRepository.findById(id);
    if (!result.isPresent()) {
      throw new DataNotFoundException();
    }
    return userMapper.user2dto(result.get());
  };

  @Override
  public UserDto findByUsername(String username) throws DataNotFoundException {
    Optional<User> result = userRepository.findByUsername(username);
    if (!result.isPresent()) {
      throw new DataNotFoundException();
    }
    return userMapper.user2dto(result.get());
  }

  @Override
  public UserDto findByUsernameAndPassword(String username, String password) throws DataNotFoundException {
    Optional<User> result = userRepository.findByUsernameAndPassword(username, password);
    if (!result.isPresent()) {
      throw new DataNotFoundException();
    }
    return userMapper.user2dto(result.get());
  }
}
