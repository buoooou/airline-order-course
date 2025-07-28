package com.postion.airlineorderbackend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 * 负责JWT令牌的生成、解析和验证
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Component
public class JwtUtil {
    
    /**
     * JWT密钥 - 从配置文件中读取
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * JWT过期时间（毫秒） - 从配置文件中读取
     */
    @Value("${jwt.expiration.ms}")
    private Long expiration;
    
    /**
     * 获取密钥对象
     * @return SecretKey对象
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * 从JWT令牌中提取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * 从JWT令牌中提取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * 从JWT令牌中提取指定的声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析器
     * @param <T> 返回类型
     * @return 声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从JWT令牌中提取所有声明
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new IllegalArgumentException("无效的JWT令牌", e);
        }
    }
    
    /**
     * 检查JWT令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * 为用户生成JWT令牌
     * @param userDetails 用户详情
     * @return JWT令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    /**
     * 为用户生成JWT令牌（带额外声明）
     * @param claims 额外声明
     * @param subject 主题（通常是用户名）
     * @return JWT令牌
     */
    public String generateToken(Map<String, Object> claims, String subject) {
        return createToken(claims, subject);
    }
    
    /**
     * 创建JWT令牌
     * @param claims 声明
     * @param subject 主题
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证JWT令牌（仅检查令牌本身）
     * @param token JWT令牌
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从JWT令牌中提取用户角色
     * @param token JWT令牌
     * @return 用户角色
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    /**
     * 从JWT令牌中获取用户名（兼容方法）
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }
    
    /**
     * 从JWT令牌中获取用户角色（兼容方法）
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        return extractRole(token);
    }
    
    /**
     * 生成带角色信息的JWT令牌
     * @param username 用户名
     * @param role 用户角色
     * @return JWT令牌
     */
    public String generateTokenWithRole(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }
    
    /**
     * 刷新JWT令牌
     * @param token 原JWT令牌
     * @return 新的JWT令牌
     */
    public String refreshToken(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            
            Map<String, Object> newClaims = new HashMap<>();
            if (role != null) {
                newClaims.put("role", role);
            }
            
            return createToken(newClaims, username);
        } catch (Exception e) {
            throw new IllegalArgumentException("无法刷新JWT令牌", e);
        }
    }
    
    /**
     * 获取JWT令牌的剩余有效时间（毫秒）
     * @param token JWT令牌
     * @return 剩余有效时间
     */
    public Long getRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0L;
        }
    }
    
    /**
     * 检查JWT令牌是否即将过期（30分钟内）
     * @param token JWT令牌
     * @return 是否即将过期
     */
    public Boolean isTokenExpiringSoon(String token) {
        try {
            Long remainingTime = getRemainingTime(token);
            return remainingTime > 0 && remainingTime < 30 * 60 * 1000; // 30分钟
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * 从Authorization头部提取JWT令牌
     * @param authorizationHeader Authorization头部值
     * @return JWT令牌，如果格式不正确则返回null
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
