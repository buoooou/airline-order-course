package com.airline.order.repository;

import com.airline.order.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据角色查找用户列表
     * @param role 角色
     * @return 用户列表
     */
    List<User> findByRole(String role);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据用户名模糊查询
     * @param username 用户名关键字
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);
    
    /**
     * 根据角色和用户名查找用户
     * @param role 角色
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByRoleAndUsername(String role, String username);
    
    /**
     * 统计指定角色的用户数量
     * @param role 角色
     * @return 用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") String role);
    
    /**
     * 查找有订单的用户
     * @return 用户列表
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.orders o")
    List<User> findUsersWithOrders();
}