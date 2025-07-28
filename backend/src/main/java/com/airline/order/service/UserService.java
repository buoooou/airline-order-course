package com.airline.order.service;

import com.airline.order.dto.UserDTO;
import com.airline.order.dto.UserRegistrationRequest;
import com.airline.order.entity.User;
import com.airline.order.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}