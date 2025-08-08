package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.LoginRequest;
import com.postion.airlineorderbackend.dto.LoginResponse;
import com.postion.airlineorderbackend.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}

