package com.position.airlineorderbackend.service;

import com.position.airlineorderbackend.dto.AuthResponse;
import com.position.airlineorderbackend.dto.LoginRequest;
import com.position.airlineorderbackend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
} 