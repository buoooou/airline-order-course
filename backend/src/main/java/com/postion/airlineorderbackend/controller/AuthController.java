package com.postion.airlineorderbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.AuthRequest;
import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "用户登录相关接口")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码登录，获取JWT令牌")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@RequestBody AuthRequest loginRequest) {
        log.info("login request: {}", loginRequest.getUsername());
        AuthResponse authResponse = userService.authenticateUser(loginRequest);

        log.info("login response: {}", authResponse.getToken());
        return ResponseEntity.ok(ApiResponse.success(authResponse));
    }
}