package com.postion.airlineorderbackend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllOrders(@RequestParam String username) {
        try {
            UserDTO userDto = userService.getUserByUsername(username);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取用户信息成功");
            result.put("data", userDto);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取用户信息失败");
            error.put("error", "GET_USER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
