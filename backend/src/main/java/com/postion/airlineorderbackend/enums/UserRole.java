package com.postion.airlineorderbackend.enums;

/**
 * 用户角色枚举
 * 定义系统中不同用户的角色和权限级别
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
public enum UserRole {
    
    /**
     * 管理员 - 拥有系统的完全访问权限
     */
    ADMIN("管理员"),
    
    /**
     * 普通用户 - 只能访问自己的订单和基本功能
     */
    USER("普通用户");
    
    private final String description;
    
    /**
     * 构造函数
     * @param description 角色描述
     */
    UserRole(String description) {
        this.description = description;
    }
    
    /**
     * 获取角色描述
     * @return 角色的中文描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为管理员角色
     * @return 是否为管理员
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * 检查是否为普通用户角色
     * @return 是否为普通用户
     */
    public boolean isUser() {
        return this == USER;
    }
    
    /**
     * 获取Spring Security使用的角色名称
     * @return Spring Security角色名称
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
