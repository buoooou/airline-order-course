package com.position.airlineorderbackend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role = "USER"; // 默认角色
} 