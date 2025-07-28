package com.postion.airlineorderbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 认证响应数据传输对象
 * 用于用户登录成功后的响应数据
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    
    /**
     * JWT访问令牌
     */
    private String accessToken;
    
    /**
     * 令牌类型，通常为 "Bearer"
     */
    private String tokenType = "Bearer";
    
    /**
     * 令牌过期时间
     */
    private LocalDateTime expiresAt;
    
    /**
     * 用户信息
     */
    private UserDTO user;
    
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
    
    /**
     * 登录成功消息
     */
    private String message;
    
    /**
     * 构造函数 - 成功登录
     * @param accessToken JWT令牌
     * @param expiresAt 过期时间
     * @param user 用户信息
     */
    public AuthResponse(String accessToken, LocalDateTime expiresAt, UserDTO user) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresAt = expiresAt;
        this.user = user;
        this.loginTime = LocalDateTime.now();
        this.message = "登录成功";
    }
    
    /**
     * 构造函数 - 成功登录（带自定义消息）
     * @param accessToken JWT令牌
     * @param expiresAt 过期时间
     * @param user 用户信息
     * @param message 自定义消息
     */
    public AuthResponse(String accessToken, LocalDateTime expiresAt, UserDTO user, String message) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresAt = expiresAt;
        this.user = user;
        this.loginTime = LocalDateTime.now();
        this.message = message;
    }
    
    /**
     * 获取完整的Authorization头部值
     * @return Authorization头部值，如 "Bearer eyJhbGciOiJIUzI1NiJ9..."
     */
    public String getAuthorizationHeader() {
        return this.tokenType + " " + this.accessToken;
    }
    
    /**
     * 检查令牌是否即将过期（30分钟内）
     * @return 是否即将过期
     */
    public boolean isTokenExpiringSoon() {
        if (this.expiresAt == null) {
            return false;
        }
        
        LocalDateTime thirtyMinutesFromNow = LocalDateTime.now().plusMinutes(30);
        return this.expiresAt.isBefore(thirtyMinutesFromNow);
    }
    
    /**
     * 获取令牌剩余有效时间（分钟）
     * @return 剩余有效时间（分钟）
     */
    public long getRemainingMinutes() {
        if (this.expiresAt == null) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (this.expiresAt.isBefore(now)) {
            return 0;
        }
        
        return java.time.Duration.between(now, this.expiresAt).toMinutes();
    }
}
