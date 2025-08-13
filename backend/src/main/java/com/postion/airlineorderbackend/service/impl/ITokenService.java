package com.postion.airlineorderbackend.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Date;

import com.postion.airlineorderbackend.dto.UserDto;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

/**
 * token验证处理
 * 
 * @author erms
 */
@Component
public class ITokenService
{
    private static final Logger log = LoggerFactory.getLogger(ITokenService.class);

    // 令牌秘钥
    @Value("${jwt.secret}")
    private String secret;

    // 令牌有效期（毫秒）
    @Value("${jwt.expiration.ms}")
    private int expireTime;

    /**
     * 创建令牌
     * 
     * @param userDto 用户信息
     * @return 令牌
     */
    public String createToken(UserDto userDto)
    {
        String token = UUID.randomUUID().toString();
        userDto.setToken(token);

        Map<String, Object> claims = new HashMap<>();
        claims.put("login_user_key", token);
        claims.put(Claims.SUBJECT, userDto.getUsername());
        return createToken(claims);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        // 计算过期时间
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expireTime);

        // 使用JJWT提供的Keys类生成符合HS512算法要求的安全密钥
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
        return token;
    }
}
