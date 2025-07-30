package com.postion.airlineorderbackend.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    
    String authenticate(String username, String password) throws AuthenticationException;
}
