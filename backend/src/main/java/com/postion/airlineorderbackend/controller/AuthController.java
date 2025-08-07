package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.AuthRequest;
import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * login
     *
     * @param request
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        // 1. Authentication username and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Get UserDetail
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Generate JWT token
        String token = jwtService.generateToken(userDetails);

        // 4. return JWT token
        return new AuthResponse(token);
    }
}
