package com.postion.airlineorderbackend.repository;

import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 提供用户相关的数据库操作方法
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * 用于登录验证和用户名唯一性检查
     * 
     * @param username 用户名
     * @return 用户信息（可能为空）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 检查用户名是否已存在
     * 用于注册时的用户名重复检查
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据角色查找用户列表
     * 用于管理员查看不同角色的用户
     * 
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> findByRole(UserRole role);
    
    /**
     * 根据用户名模糊查询用户
     * 用于用户搜索功能
     * 
     * @param username 用户名关键字
     * @return 匹配的用户列表
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);
    
    /**
     * 查找所有管理员用户
     * 用于系统管理功能
     * 
     * @return 管理员用户列表
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdmins();
    
    /**
     * 查找所有普通用户
     * 用于用户管理功能
     * 
     * @return 普通用户列表
     */
    @Query("SELECT u FROM User u WHERE u.role = 'USER'")
    List<User> findAllUsers();
    
    /**
     * 统计不同角色的用户数量
     * 用于系统统计功能
     * 
     * @param role 用户角色
     * @return 用户数量
     */
    long countByRole(UserRole role);
    
    /**
     * 查找有订单的用户
     * 用于分析活跃用户
     * 
     * @return 有订单的用户列表
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.orders o")
    List<User> findUsersWithOrders();
    
    /**
     * 根据用户ID列表批量查询用户
     * 用于批量操作
     * 
     * @param userIds 用户ID列表
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findByIdIn(@Param("userIds") List<Long> userIds);
    
    /**
     * 查找用户名以指定前缀开头的用户
     * 用于特定的用户筛选
     * 
     * @param prefix 用户名前缀
     * @return 匹配的用户列表
     */
    List<User> findByUsernameStartingWith(String prefix);
    
    /**
     * 根据用户名和角色查找用户
     * 用于精确查询
     * 
     * @param username 用户名
     * @param role 用户角色
     * @return 用户信息（可能为空）
     */
    Optional<User> findByUsernameAndRole(String username, UserRole role);
}
