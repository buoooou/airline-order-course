package com.postion.airlineorderbackend.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;


    // 无参构造 (反序列化 JSON 需要)
    public AuthRequest() {
    }

    // 有参构造 (方便测试)
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 和 Setter
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
}