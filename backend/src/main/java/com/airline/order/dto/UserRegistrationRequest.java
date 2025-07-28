package com.airline.order.dto;

/**
 * 用户注册请求DTO
 */
public class UserRegistrationRequest {
    
    private String username;
    private String password;
    private String role;
    
    // 构造函数
    public UserRegistrationRequest() {
    }
    
    public UserRegistrationRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Getter和Setter方法
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "UserRegistrationRequest{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}