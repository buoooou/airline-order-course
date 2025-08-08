package com.position.airline_order_course.dto;

import lombok.Data;

/*
 * 登录请求DTO
 */
@Data
public class LoginRequestDto {
    private String username;
    private String password;
}