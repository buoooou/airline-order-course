package com.airline.order.service;

import com.airline.order.dto.UserDTO;
import com.airline.order.dto.UserRegistrationRequest;
import com.airline.order.entity.User;
import com.airline.order.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册后的用户DTO
     * @throws RuntimeException 当用户名已存在时抛出异常
     */
    public UserDTO registerUser(UserRegistrationRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        
        User savedUser = userRepository.save(user);
        
        // 转换为DTO
        return convertToDTO(savedUser);
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户DTO
     * @throws RuntimeException 当用户不存在或密码错误时抛出异常
     */
    public UserDTO loginUser(String username, String password) {
        // 查找用户
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOptional.get();
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 转换为DTO
        return convertToDTO(user);
    }
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户DTO
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    public UserDTO getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        return convertToDTO(userOptional.get());
    }
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return true表示可用，false表示已存在
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    /**
     * 根据角色查询用户列表
     * @param role 角色
     * @return 用户DTO列表
     */
    public List<UserDTO> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 根据用户名模糊查询用户
     * @param username 用户名关键字
     * @return 用户DTO列表
     */
    public List<UserDTO> searchUsersByUsername(String username) {
        List<User> users = userRepository.findByUsernameContaining(username);
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 根据角色和用户名查询用户
     * @param role 角色
     * @param username 用户名
     * @return 用户DTO
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    public UserDTO getUserByRoleAndUsername(String role, String username) {
        Optional<User> userOptional = userRepository.findByRoleAndUsername(role, username);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        return convertToDTO(userOptional.get());
    }
    
    /**
     * 统计指定角色的用户数量
     * @param role 角色
     * @return 用户数量
     */
    public long countUsersByRole(String role) {
        return userRepository.countByRole(role);
    }
    
    /**
     * 查找有订单的用户
     * @return 有订单的用户DTO列表
     */
    public List<UserDTO> getUsersWithOrders() {
        List<User> users = userRepository.findUsersWithOrders();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 获取所有用户
     * @return 所有用户DTO列表
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户DTO
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    public UserDTO getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        return convertToDTO(userOptional.get());
    }
    
    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param newRole 新角色
     * @return 更新后的用户DTO
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    public UserDTO updateUserRole(Long userId, String newRole) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOptional.get();
        user.setRole(newRole);
        User savedUser = userRepository.save(user);
        
        return convertToDTO(savedUser);
    }
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 更新后的用户DTO
     * @throws RuntimeException 当用户不存在或旧密码错误时抛出异常
     */
    public UserDTO updateUserPassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOptional.get();
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);
        
        return convertToDTO(savedUser);
    }
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        
        userRepository.deleteById(userId);
    }
    
    /**
     * 获取用户统计信息
     * @return 包含各角色用户数量的统计信息
     */
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long adminUsers = userRepository.countByRole("ADMIN");
        long regularUsers = userRepository.countByRole("USER");
        long usersWithOrders = userRepository.findUsersWithOrders().size();
        
        UserStatistics stats = new UserStatistics();
        stats.setTotalUsers(totalUsers);
        stats.setAdminUsers(adminUsers);
        stats.setRegularUsers(regularUsers);
        stats.setUsersWithOrders(usersWithOrders);
        
        return stats;
    }
    
    /**
     * 将User实体转换为UserDTO
     * @param user 用户实体
     * @return 用户DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
    
    /**
     * 用户统计信息类
     */
    public static class UserStatistics {
        private long totalUsers;
        private long adminUsers;
        private long regularUsers;
        private long usersWithOrders;
        
        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        
        public long getAdminUsers() { return adminUsers; }
        public void setAdminUsers(long adminUsers) { this.adminUsers = adminUsers; }
        
        public long getRegularUsers() { return regularUsers; }
        public void setRegularUsers(long regularUsers) { this.regularUsers = regularUsers; }
        
        public long getUsersWithOrders() { return usersWithOrders; }
        public void setUsersWithOrders(long usersWithOrders) { this.usersWithOrders = usersWithOrders; }
    }
}