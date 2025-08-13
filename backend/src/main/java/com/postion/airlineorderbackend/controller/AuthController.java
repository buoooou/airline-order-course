package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.dto.LoginRequest;
import com.postion.airlineorderbackend.dto.RegisterRequest;
import com.postion.airlineorderbackend.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.util.concurrent.RateLimiter;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RateLimiter rateLimiter = RateLimiter.create(10.0); // 每秒10次

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        if (!rateLimiter.tryAcquire()) {
            log.warn("Rate limit exceeded for user: {}", loginRequest.getUsernameOrEmail());
            return ResponseEntity.status(429).body(ApiResponse.error(429,"Rate limit exceeded"));
           
        }
        String username = loginRequest.getUsernameOrEmail();
        String password = loginRequest.getPassword();
        log.info("Login attempt for user: {}", username);
        AuthResponse authResponse = authService.login(username, password);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        
        log.info("Register attempt for user: {}", username);
        String result = authService.register(username, email, password);
        if (result.equals("Username already exists") || result.equals("Email already exists")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}
