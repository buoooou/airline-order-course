package com.postion.airlineorderbackend.dto;

/**
 * 认证响应数据传输对象
 * 用于封装用户登录或注册成功后的认证信息
 */
public record AuthResponse(
        /**
         * JWT访问令牌
         * 用于后续API请求的认证
         */
        String token,

        /**
         * 令牌类型
         * 通常为"Bearer"
         */
        String type,

        /**
         * 用户名
         * 当前认证用户的用户名
         */
        String username) {
}