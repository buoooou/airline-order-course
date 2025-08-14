package com.postion.airlineorderbackend.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.service.JwtService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
	@Value("${jwt.secret}")
    private String SECRET;
	
	@Value("${jwt.expiration.ms}")
	private long EXPIRATION;

	@Override
	public String extractUsername(String jwt) {
        return Jwts.parser()
                .verifyWith(getSignInKey()).build()
                .parseSignedClaims(jwt).getPayload()
                .getSubject();
	}

	@Override
	public boolean isTokenValid(String jwt, UserDetails userDetails) {
		final String username = extractUsername(jwt);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
	}
	
	@Override
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<String, Object>();
        return createToken(claims, username);
    }
	
	private boolean isTokenExpired(String jwt) {
        return Jwts.parser()
                .verifyWith(getSignInKey()).build()
                .parseSignedClaims(jwt).getPayload()
        		.getExpiration().before(new Date());
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignInKey())
                .compact();
    }
    
    private SecretKey getSignInKey() {
    	String encodedSecret = Base64.getEncoder().encodeToString(SECRET.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Base64.getDecoder().decode(encodedSecret);

        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
    
}
