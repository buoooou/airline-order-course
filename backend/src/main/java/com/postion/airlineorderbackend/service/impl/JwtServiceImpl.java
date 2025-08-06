package com.postion.airlineorderbackend.service.impl;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService{


    /**
     * 从配置文件读取 base64 或明文密钥
     */
    @Value("${jwt.secret}")
    private String secret;//secretKeyString

    @Value("${jwt.expiration.ms}")
    private long jwtExpiration;

//    @Value("${spring.security.jwt.expiration:86400000}") // 默认 24h
//    private long expirationMs;

    /**
     * 从 token 中取出用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 生成 JWT
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date(System.currentTimeMillis());
        Date exp = new Date(System.currentTimeMillis() + jwtExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())              // 用户名
                .issuedAt(now)                  // 签发时间
                .expiration(exp)                // 过期时间
                .signWith(getSigningKey())      // 签名算法 & 密钥
                .compact();
    }
    
    /**
     * 判断 token 是否有效（未过期 & 用户名匹配）
     */
    public boolean isTokenVaild(String token,UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 判断 token 是否有效（未过期 & 用户名匹配）
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaim(token).getExpiration().before(new Date());
    }

    /**
     * 生成安全的 HS256 Key（jjwt 0.12.x 推荐方式）
     */
    private SecretKey getSigningKey() {
        // 长度必须 ≥ 256 位（32 字节），否则 HS256 会报错
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 解析并验证 JWT，返回负载
     */
    public Claims extractAllClaim(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())    // 设置验证密钥
                .build()
                .parseSignedClaims(token)       // 解析 + 签名校验
                .getPayload();                  // 等价于 getBody()
    }
}
