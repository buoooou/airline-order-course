package com.postion.airlineorderbackend.controller;


import com.postion.airlineorderbackend.dto.LoginUserDetails;
import com.postion.airlineorderbackend.payload.JwtResponse;
import com.postion.airlineorderbackend.payload.LoginRequest;
import com.postion.airlineorderbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());
        LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwtToken, userDetails));
    }
}    
