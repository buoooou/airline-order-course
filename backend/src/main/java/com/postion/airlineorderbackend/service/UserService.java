package com.postion.airlineorderbackend.service;

import java.util.List;

import com.postion.airlineorderbackend.dto.UserDto;

public interface UserService {

  /**
   * Get all users.
   * 
   * @return All users.
   */
  List<UserDto> getAllUsers();

  /**
   * Find user by user id.
   * 
   * @param id The user id.
   * @return The user. Return null if not found.
   */
  UserDto findByUserId(Long id);

  /**
   * Find users with specified user name.
   * 
   * @param username The user name.
   * @return List of the users.
   */
  List<UserDto> findByUsername(String username);

}
