package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.model.User;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {
    
    /**
     * 通过用户名查找用户
     * @param username 用户名
     * @return 用户信息
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    User findByUsername(String username);
    
    /**
     * 获取当前登录用户
     * @return 当前用户信息
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    User getCurrentUser();
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户信息
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    User findById(Long id);
    
    /**
     * 创建新用户
     * @param user 用户信息
     * @return 创建的用户
     * @throws RuntimeException 当用户名或邮箱已存在时抛出异常
     */
    User createUser(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);
}