package com.postion.airlineorderbackend.dto;

import lombok.Data;

/**
 * 用户数据传输对象
 * 用于用户相关API接口的数据传输
 * 
 * @author Postion
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class UserDto {
    /**
     * 用户唯一标识
     */
    private Long id;
    
    /**
     * 用户名
     * 唯一标识用户的登录名
     */
    private String username;
    
    /**
     * 密码
     * 用户登录密码（加密存储）
     */
    private String password;
    
    /**
     * 用户角色
     * 包括：USER（普通用户）、ADMIN（管理员）
     */
    private String role;
}
