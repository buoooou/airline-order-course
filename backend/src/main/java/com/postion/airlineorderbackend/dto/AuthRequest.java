package com.postion.airlineorderbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 认证请求数据传输对象
 * 用于用户登录时的请求参数
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    
    /**
     * 用户名 - 不能为空，长度限制
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    /**
     * 密码 - 不能为空，长度限制
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;
    
    /**
     * 记住我 - 可选，用于延长token有效期
     */
    private Boolean rememberMe = false;
}
