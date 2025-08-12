package com.postion.airlineorderbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import com.postion.airlineorderbackend.entity.User;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration.ms}")
    private long jwtExpirationMs;

    private SecretKey secretKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
    	byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
        secretKey = Keys.hmacShaKeyFor(decodedKey);
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    // 生成 token
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // 获取用户名
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getEmailFromToken(String token) {
    	Object email = parseClaims(token).get("email");
    	return email != null ? email.toString() : null;
    }
    
    // 验证 token
    public boolean validateToken(String token) {
        try {
        	parseClaims(token); //+setExpiration(...):自动完成校验Token签名与过期时间
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
    
    
 // 私有方法统一解析 claims
    private Claims parseClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
    	return jwtParser.parseSignedClaims(token).getPayload();
    }

}
