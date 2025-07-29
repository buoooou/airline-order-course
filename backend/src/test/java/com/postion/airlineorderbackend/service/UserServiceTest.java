package com.postion.airlineorderbackend.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.postion.airlineorderbackend.dto.UserDto;

@SpringBootTest
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("test userService.getAllOrders(), should get all orders")
  void shouldGetAllUsers() {
    List<UserDto> allUsers = userService.getAllUsers();
    assertNotNull(allUsers);
    assertTrue(allUsers.size() > 0);
  }

}
