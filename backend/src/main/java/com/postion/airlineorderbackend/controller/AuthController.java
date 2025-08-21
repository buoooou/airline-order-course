package com.postion.airlineorderbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.constants.Constants;
import com.postion.airlineorderbackend.dto.ApiResponseDTO;
import com.postion.airlineorderbackend.dto.AuthRequestDTO;
import com.postion.airlineorderbackend.dto.AuthResponseDTO;
import com.postion.airlineorderbackend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponseDTO<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        try {
            AuthResponseDTO authResponseDto = authService.authenticate(request.getUsername(), request.getPassword());
            
            return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.LOGIN_SUCCESS, authResponseDto);
        } catch (AuthenticationException e) {
            log.warn("AuthController# Catched AuthenticationException. username:{}, password:{}", request.getUsername(), request.getPassword());
            System.out.println("AuthController# Catched AuthenticationException. username:" + request.getUsername() + ", password:" + request.getPassword());
            return ApiResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), Constants.LOGIN_FAIL);
        }
    }
}
