package com.postion.airlineorderbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;

    // 无参构造（给序列化/反序列化用，比如Jackson需要）
    public AuthResponse() {
    }

    // 有参构造（解决编译错误）
    public AuthResponse(String token) {
        this.token = token;
    }

    // Getter & Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}