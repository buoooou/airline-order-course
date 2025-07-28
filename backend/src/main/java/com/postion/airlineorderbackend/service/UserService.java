package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.enums.UserRole;
import com.postion.airlineorderbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务类
 * 提供用户相关的业务逻辑处理
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Spring Security用户详情加载方法
     * @param username 用户名
     * @return UserDetails对象
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("正在加载用户详情: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthority())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.debug("查找用户: {}", username);
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根据用户ID查找用户
     * @param id 用户ID
     * @return 用户实体（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        log.debug("根据ID查找用户: {}", id);
        return userRepository.findById(id);
    }
    
    /**
     * 创建新用户
     * @param username 用户名
     * @param password 密码（明文）
     * @param role 用户角色
     * @return 创建的用户实体
     * @throws IllegalArgumentException 如果用户名已存在
     */
    public User createUser(String username, String password, UserRole role) {
        log.info("创建新用户: {}, 角色: {}", username, role);
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在: " + username);
        }
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(password);
        
        // 创建用户实体
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .role(role)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("用户创建成功: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    /**
     * 验证用户密码
     * @param username 用户名
     * @param rawPassword 原始密码
     * @return 是否验证成功
     */
    @Transactional(readOnly = true)
    public boolean validatePassword(String username, String rawPassword) {
        log.debug("验证用户密码: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在: {}", username);
            return false;
        }
        
        User user = userOpt.get();
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        
        if (matches) {
            log.debug("用户密码验证成功: {}", username);
        } else {
            log.warn("用户密码验证失败: {}", username);
        }
        
        return matches;
    }
    
    /**
     * 更新用户密码
     * @param username 用户名
     * @param newPassword 新密码（明文）
     * @return 是否更新成功
     */
    public boolean updatePassword(String username, String newPassword) {
        log.info("更新用户密码: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在，无法更新密码: {}", username);
            return false;
        }
        
        User user = userOpt.get();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        
        userRepository.save(user);
        log.info("用户密码更新成功: {}", username);
        
        return true;
    }
    
    /**
     * 获取所有用户列表
     * @return 用户DTO列表
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.debug("获取所有用户列表");
        
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据角色获取用户列表
     * @param role 用户角色
     * @return 用户DTO列表
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(UserRole role) {
        log.debug("根据角色获取用户列表: {}", role);
        
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据用户名模糊查询用户
     * @param username 用户名关键字
     * @return 用户DTO列表
     */
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsersByUsername(String username) {
        log.debug("根据用户名模糊查询: {}", username);
        
        List<User> users = userRepository.findByUsernameContaining(username);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取管理员用户列表
     * @return 管理员用户DTO列表
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAdminUsers() {
        log.debug("获取管理员用户列表");
        
        List<User> admins = userRepository.findAllAdmins();
        return admins.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取普通用户列表
     * @return 普通用户DTO列表
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getRegularUsers() {
        log.debug("获取普通用户列表");
        
        List<User> users = userRepository.findAllUsers();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 统计不同角色的用户数量
     * @param role 用户角色
     * @return 用户数量
     */
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        log.debug("统计角色用户数量: {}", role);
        return userRepository.countByRole(role);
    }
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUser(Long id) {
        log.info("删除用户: {}", id);
        
        if (!userRepository.existsById(id)) {
            log.warn("用户不存在，无法删除: {}", id);
            return false;
        }
        
        userRepository.deleteById(id);
        log.info("用户删除成功: {}", id);
        return true;
    }
    
    /**
     * 更新用户角色
     * @param username 用户名
     * @param newRole 新角色
     * @return 是否更新成功
     */
    public boolean updateUserRole(String username, UserRole newRole) {
        log.info("更新用户角色: {} -> {}", username, newRole);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在，无法更新角色: {}", username);
            return false;
        }
        
        User user = userOpt.get();
        user.setRole(newRole);
        
        userRepository.save(user);
        log.info("用户角色更新成功: {} -> {}", username, newRole);
        
        return true;
    }
    
    /**
     * 将User实体转换为UserDTO
     * @param user 用户实体
     * @return 用户DTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .roleDescription(user.getRole() != null ? user.getRole().getDescription() : "")
                .build();
    }
    
    /**
     * 根据用户名获取用户DTO
     * @param username 用户名
     * @return 用户DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserDTOByUsername(String username) {
        log.debug("获取用户DTO: {}", username);
        
        return userRepository.findByUsername(username)
                .map(this::convertToDTO);
    }
    
    /**
     * 根据用户ID获取用户DTO
     * @param id 用户ID
     * @return 用户DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserDTOById(Long id) {
        log.debug("根据ID获取用户DTO: {}", id);
        
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }
}
