package com.postion.airlineorderbackend.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.AuthResponseDTO;

@Service
public interface AuthService {
    
    AuthResponseDTO authenticate(String username, String password) throws AuthenticationException;
}
