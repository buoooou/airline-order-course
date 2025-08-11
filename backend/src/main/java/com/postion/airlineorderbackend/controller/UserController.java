package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.BaseResponse;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户接口", description = "用户相关操作")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 构造函数注入
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取所有用户列表")
    @GetMapping
    public ResponseEntity<BaseResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, users));
    }
}

