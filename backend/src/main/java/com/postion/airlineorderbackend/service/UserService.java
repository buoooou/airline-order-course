package com.postion.airlineorderbackend.service;

import java.util.List;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.exception.DataNotFoundException;

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
   * @return Found user.
   * @throws DataNotFoundException
   */
  UserDto findByUserId(Long id) throws DataNotFoundException;

  /**
   * Find users with specified user name.
   * 
   * @param username The user name.
   * @return Found user.
   * @throws DataNotFoundException
   */
  UserDto findByUsername(String username) throws DataNotFoundException;

  /**
   * Find users with specified user name and password.
   * 
   * @param username The user name.
   * @param password The password.
   * @return Found user.
   * @throws DataNotFoundException
   */
  UserDto findByUsernameAndPassword(String username, String password) throws DataNotFoundException;

}
