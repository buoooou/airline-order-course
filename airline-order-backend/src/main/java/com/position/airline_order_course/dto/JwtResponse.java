package com.position.airline_order_course.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
 * 用户认证返回的DTO
 */
@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String username;
    private List<String> roles;

}