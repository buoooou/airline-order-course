package com.airline.order.dto;

import java.util.List;

/**
 * JWT响应类
 * 用于封装JWT令牌和用户信息
 */
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String role;

    /**
     * 构造函数
     * @param token JWT令牌
     * @param id 用户ID
     * @param username 用户名
     * @param role 用户角色
     */
    public JwtResponse(String token, Long id, String username, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.role = role;
    }

    /**
     * 获取令牌类型
     * @return 令牌类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置令牌类型
     * @param type 令牌类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取令牌
     * @return 令牌
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置令牌
     * @param token 令牌
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取用户角色
     * @return 用户角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置用户角色
     * @param role 用户角色
     */
    public void setRole(String role) {
        this.role = role;
    }
}