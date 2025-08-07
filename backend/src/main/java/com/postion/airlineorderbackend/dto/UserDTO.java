package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.enums.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 用户数据传输对象
 * 用于前后端数据交互，不包含敏感信息如密码
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户角色
     */
    private UserRole role;
    
    /**
     * 用户角色描述
     */
    private String roleDescription;
    
    /**
     * 构造函数 - 从实体转换
     * @param id 用户ID
     * @param username 用户名
     * @param role 用户角色
     */
    public UserDTO(Long id, String username, UserRole role) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.roleDescription = role != null ? role.getDescription() : "";
    }
    
    /**
     * 检查用户是否为管理员
     * @return 是否为管理员
     */
    public boolean isAdmin() {
        return this.role != null && this.role.isAdmin();
    }
    
    /**
     * 检查用户是否为普通用户
     * @return 是否为普通用户
     */
    public boolean isUser() {
        return this.role != null && this.role.isUser();
    }
}
