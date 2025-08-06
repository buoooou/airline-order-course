package com.postion.airlineorderbackend.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.exception.UserNotFoundException;
import com.postion.airlineorderbackend.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * Utility fro Authentication.
 */
@Component
@RequiredArgsConstructor
public class AuthUtil {

  private final UserService userService;

  /**
   * Get user details from Spring authentication.
   * 
   * @param auth Spring authentication.
   * @return UserDto
   */
  public UserDto getUserDetails(Authentication auth) throws UserNotFoundException {
    try {
      return userService.findByUsername(((UserDetails) auth.getPrincipal()).getUsername());
    } catch (DataNotFoundException e) {
      throw new UserNotFoundException();
    }
  }

}
