package com.postion.airlineorderbackend.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.postion.airlineorderbackend.dto.UserDto;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
public class UserServiceTest {

  private final UserService userService;

  @Test
  @DisplayName("test userService.getAllOrders(), should get all orders")
  void shouldGetAllUsers() {
    List<UserDto> allUsers = userService.getAllUsers();
    assertNotNull(allUsers);
    assertTrue(allUsers.size() > 0);
  }

}
