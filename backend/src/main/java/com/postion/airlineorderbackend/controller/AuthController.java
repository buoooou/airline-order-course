package com.postion.airlineorderbackend.controller;

import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticatedAuthorizationManager auth;

    
}
