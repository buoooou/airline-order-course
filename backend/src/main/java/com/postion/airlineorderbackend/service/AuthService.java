package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.AuthRequest;
import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 认证服务类
 * 提供用户登录、登出、令牌验证等认证相关功能
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户登录
     * @param authRequest 登录请求
     * @return 认证响应
     * @throws BadCredentialsException 认证失败异常
     */
    public AuthResponse login(AuthRequest authRequest) {
        log.info("用户登录请求: {}", authRequest.getUsername());
        
        try {
            // 进行身份验证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
            
            // 获取用户详情
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 获取用户实体
            Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
            if (userOpt.isEmpty()) {
                log.error("认证成功但用户不存在: {}", userDetails.getUsername());
                throw new BadCredentialsException("用户不存在");
            }
            
            User user = userOpt.get();
            
            // 生成JWT令牌
            String token = jwtUtil.generateTokenWithRole(
                    user.getUsername(),
                    user.getRole().name()
            );
            
            // 计算令牌过期时间
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                    jwtUtil.getRemainingTime(token) / 1000
            );
            
            // 创建用户DTO
            UserDTO userDTO = UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .roleDescription(user.getRole().getDescription())
                    .build();
            
            // 创建认证响应
            AuthResponse response = AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresAt(expiresAt)
                    .user(userDTO)
                    .loginTime(LocalDateTime.now())
                    .message("登录成功")
                    .build();
            
            log.info("用户登录成功: {}, 角色: {}", user.getUsername(), user.getRole());
            return response;
            
        } catch (AuthenticationException e) {
            log.warn("用户登录失败: {}, 原因: {}", authRequest.getUsername(), e.getMessage());
            throw new BadCredentialsException("用户名或密码错误");
        }
    }
    
    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @return 是否有效
     */
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        log.debug("验证JWT令牌");
        
        try {
            if (!jwtUtil.validateToken(token)) {
                log.debug("JWT令牌验证失败");
                return false;
            }
            
            String username = jwtUtil.extractUsername(token);
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                log.warn("JWT令牌中的用户不存在: {}", username);
                return false;
            }
            
            log.debug("JWT令牌验证成功: {}", username);
            return true;
            
        } catch (Exception e) {
            log.error("JWT令牌验证异常", e);
            return false;
        }
    }
    
    /**
     * 验证JWT令牌并返回用户信息
     * @param token JWT令牌
     * @return 用户DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> validateTokenAndGetUser(String token) {
        log.debug("验证JWT令牌并获取用户信息");
        
        try {
            if (!jwtUtil.validateToken(token)) {
                log.debug("JWT令牌验证失败");
                return Optional.empty();
            }
            
            String username = jwtUtil.extractUsername(token);
            Optional<UserDTO> userDTO = userService.getUserDTOByUsername(username);
            
            if (userDTO.isEmpty()) {
                log.warn("JWT令牌中的用户不存在: {}", username);
                return Optional.empty();
            }
            
            log.debug("JWT令牌验证成功，用户: {}", username);
            return userDTO;
            
        } catch (Exception e) {
            log.error("JWT令牌验证异常", e);
            return Optional.empty();
        }
    }
    
    /**
     * 刷新JWT令牌
     * @param token 原JWT令牌
     * @return 新的认证响应
     * @throws IllegalArgumentException 令牌无效异常
     */
    public AuthResponse refreshToken(String token) {
        log.info("刷新JWT令牌");
        
        try {
            // 验证原令牌
            if (!jwtUtil.validateToken(token)) {
                throw new IllegalArgumentException("无效的JWT令牌");
            }
            
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            
            // 获取用户信息
            Optional<UserDTO> userDTOOpt = userService.getUserDTOByUsername(username);
            if (userDTOOpt.isEmpty()) {
                throw new IllegalArgumentException("用户不存在");
            }
            
            UserDTO userDTO = userDTOOpt.get();
            
            // 生成新令牌
            String newToken = jwtUtil.generateTokenWithRole(username, role);
            
            // 计算新令牌过期时间
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                    jwtUtil.getRemainingTime(newToken) / 1000
            );
            
            // 创建认证响应
            AuthResponse response = AuthResponse.builder()
                    .accessToken(newToken)
                    .tokenType("Bearer")
                    .expiresAt(expiresAt)
                    .user(userDTO)
                    .loginTime(LocalDateTime.now())
                    .message("令牌刷新成功")
                    .build();
            
            log.info("JWT令牌刷新成功: {}", username);
            return response;
            
        } catch (Exception e) {
            log.error("JWT令牌刷新失败", e);
            throw new IllegalArgumentException("令牌刷新失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户登出
     * 注意：JWT是无状态的，实际的登出需要在客户端删除令牌
     * 这里主要用于记录日志
     * 
     * @param token JWT令牌
     * @return 登出是否成功
     */
    public boolean logout(String token) {
        log.info("用户登出");
        
        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                log.info("用户登出成功: {}", username);
                return true;
            } else {
                log.warn("登出时JWT令牌无效");
                return false;
            }
        } catch (Exception e) {
            log.error("用户登出异常", e);
            return false;
        }
    }
    
    /**
     * 检查JWT令牌是否即将过期
     * @param token JWT令牌
     * @return 是否即将过期
     */
    @Transactional(readOnly = true)
    public boolean isTokenExpiringSoon(String token) {
        try {
            return jwtUtil.isTokenExpiringSoon(token);
        } catch (Exception e) {
            log.error("检查令牌过期时间异常", e);
            return true; // 异常时认为即将过期
        }
    }
    
    /**
     * 获取JWT令牌的剩余有效时间
     * @param token JWT令牌
     * @return 剩余有效时间（毫秒）
     */
    @Transactional(readOnly = true)
    public Long getTokenRemainingTime(String token) {
        try {
            return jwtUtil.getRemainingTime(token);
        } catch (Exception e) {
            log.error("获取令牌剩余时间异常", e);
            return 0L;
        }
    }
    
    /**
     * 从Authorization头部提取并验证JWT令牌
     * @param authorizationHeader Authorization头部值
     * @return 用户DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> validateAuthorizationHeader(String authorizationHeader) {
        log.debug("验证Authorization头部");
        
        String token = jwtUtil.extractTokenFromHeader(authorizationHeader);
        if (token == null) {
            log.debug("Authorization头部格式不正确");
            return Optional.empty();
        }
        
        return validateTokenAndGetUser(token);
    }
    
    /**
     * 检查用户是否有指定角色
     * @param token JWT令牌
     * @param requiredRole 需要的角色
     * @return 是否有权限
     */
    @Transactional(readOnly = true)
    public boolean hasRole(String token, String requiredRole) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return false;
            }
            
            String userRole = jwtUtil.extractRole(token);
            return requiredRole.equals(userRole);
            
        } catch (Exception e) {
            log.error("检查用户角色异常", e);
            return false;
        }
    }
    
    /**
     * 检查用户是否为管理员
     * @param token JWT令牌
     * @return 是否为管理员
     */
    @Transactional(readOnly = true)
    public boolean isAdmin(String token) {
        return hasRole(token, "ADMIN");
    }
    
    /**
     * 获取当前认证用户信息
     * @param token JWT令牌
     * @return 用户DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getCurrentUser(String token) {
        return validateTokenAndGetUser(token);
    }
}
