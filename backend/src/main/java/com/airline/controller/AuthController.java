package com.airline.controller;

import com.airline.dto.ApiResponse;
import com.airline.dto.UserLoginDto;
import com.airline.dto.UserRegistrationDto;
import com.airline.dto.UserDto;
import com.airline.service.AuthService;
import com.airline.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户认证相关API")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public ResponseEntity<ApiResponse<AuthService.AuthResponse>> login(@Valid @RequestBody UserLoginDto loginDto) {
        AuthService.AuthResponse authResponse = authService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("登录成功", authResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserDto user = userService.registerUser(registrationDto);
        return ResponseEntity.ok(ApiResponse.success("注册成功", user));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public ResponseEntity<ApiResponse<AuthService.AuthResponse>> refreshToken(@RequestParam String refreshToken) {
        AuthService.AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("令牌刷新成功", authResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("登出成功"));
    }
}