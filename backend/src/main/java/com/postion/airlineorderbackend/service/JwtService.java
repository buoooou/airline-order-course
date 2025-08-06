package com.postion.airlineorderbackend.service;

import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String extractUsername(String token);
    <T> T extractClaim(String token, Function<Claims,T> claimsResolver);
    String generateToken(UserDetails userDetails);
    boolean isTokenVaild(String token, UserDetails userDetails);

}
