package com.postion.airlineorderbackend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
 * <p>
 * 提供JWT令牌的生成、解析和验证功能
 * 用于实现用户认证和授权的令牌管理
 * </p>
 */
@Component
public class JwtUtil {

    /**
     * JWT签名密钥
     * <p>
     * 从配置文件中读取，用于令牌签名验证
     * </p>
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT令牌有效期（毫秒）
     * <p>
     * 从配置文件中读取，定义令牌的有效时长
     * </p>
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 获取JWT签名密钥
     * <p>
     * 使用HMAC-SHA算法生成签名密钥
     * </p>
     * 
     * @return SecretKey 签名密钥对象
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 从JWT令牌中获取用户名
     * <p>
     * 解析令牌并提取subject字段作为用户名
     * </p>
     * 
     * @param token JWT令牌字符串
     * @return String 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 获取JWT令牌的过期时间
     * <p>
     * 解析令牌并提取expiration字段
     * </p>
     * 
     * @param token JWT令牌字符串
     * @return Date 令牌过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从JWT令牌中获取指定声明
     * <p>
     * 使用函数式接口从令牌声明中提取指定字段
     * </p>
     * 
     * @param token          JWT令牌字符串
     * @param claimsResolver 声明解析函数
     * @param <T>            声明值的类型
     * @return T 指定声明的值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析JWT令牌获取所有声明
     * <p>
     * 使用签名密钥验证并解析令牌，获取完整的声明信息
     * </p>
     * 
     * @param token JWT令牌字符串
     * @return Claims 令牌的所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    /**
     * 检查JWT令牌是否已过期
     * <p>
     * 比较令牌的过期时间与当前时间
     * </p>
     * 
     * @param token JWT令牌字符串
     * @return Boolean true表示令牌已过期，false表示令牌有效
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成JWT令牌
     * <p>
     * 根据用户详情生成包含用户名的JWT令牌
     * </p>
     * 
     * @param userDetails 用户详情对象
     * @return String 生成的JWT令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 创建JWT令牌
     * <p>
     * 根据声明和主题创建完整的JWT令牌，包含发行时间、过期时间和签名
     * </p>
     * 
     * @param claims  令牌声明信息
     * @param subject 令牌主题（通常是用户名）
     * @return String 完整的JWT令牌字符串
     */
    private String createToken(Map<String, Object> claims, String subject) {
        long currentLong = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(currentLong))
                .expiration(new Date(currentLong + expiration))
                .signWith(getSigningKey())
                .compact();
    };

    /**
     * 验证JWT令牌的有效性
     * <p>
     * 验证令牌中的用户名与用户详情是否匹配，并检查令牌是否过期
     * </p>
     * 
     * @param token       JWT令牌字符串
     * @param userDetails 用户详情对象
     * @return Boolean true表示令牌有效，false表示令牌无效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}