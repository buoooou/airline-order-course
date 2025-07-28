package com.postion.airlineorderbackend.entity;

import com.postion.airlineorderbackend.enums.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.util.List;

/**
 * 用户实体类
 * 对应数据库表 app_users_qiaozhe
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Entity
@Table(name = "app_users_qiaozhe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    /**
     * 用户ID - 主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 用户名 - 唯一，不能为空
     */
    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;
    
    /**
     * 密码 - 加密存储，不能为空
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    /**
     * 用户角色 - 枚举类型，不能为空
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role;
    
    /**
     * 用户的订单列表 - 一对多关系
     * 一个用户可以有多个订单
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
    
    /**
     * 构造函数 - 用于创建新用户
     * @param username 用户名
     * @param password 密码（应该是加密后的）
     * @param role 用户角色
     */
    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
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
    
    /**
     * 获取用户角色的权限字符串
     * @return 权限字符串
     */
    public String getAuthority() {
        return this.role != null ? this.role.getAuthority() : "";
    }
    
    /**
     * toString方法 - 不包含密码信息，保证安全
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
