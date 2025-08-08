package com.postion.airlineorderbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.BaseResponse;
import com.postion.airlineorderbackend.dto.LoginRequest;
import com.postion.airlineorderbackend.dto.LoginResponse;
import com.postion.airlineorderbackend.dto.RegisterRequest;
import com.postion.airlineorderbackend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "认证接口", description = "用户注册与登录相关操作")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "注册用户", description = "注册一个新用户")
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, "User registered successfully"));
    }
    
    @Operation(summary = "用户登录", description = "用户使用凭证登录，返回JWT令牌")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
    	LoginResponse response = authService.login(request);
    	return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, response));
    	
    }
}
