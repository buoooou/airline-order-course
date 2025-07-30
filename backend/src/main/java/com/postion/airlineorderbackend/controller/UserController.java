package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    // 根据用户名查询用户
    @GetMapping("/getUserByUsername")
    public UserDto getUserByUsername(@RequestParam String username) {
        return userService.getUserByUsername(username);
    }

    // 根据用户Id查询用户
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
