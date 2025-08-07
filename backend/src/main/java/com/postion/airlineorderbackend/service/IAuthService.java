package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.AuthRequest;
import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.dto.UserDTO;

import java.util.Optional;

/**
 * 认证服务接口
 * 定义用户认证相关的业务逻辑
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
public interface IAuthService {
    
    /**
     * 用户登录
     * 
     * @param authRequest 登录请求
     * @return 认证响应，包含JWT令牌和用户信息
     * @throws org.springframework.security.authentication.BadCredentialsException 当用户名或密码错误时
     */
    AuthResponse login(AuthRequest authRequest);
    
    /**
     * 验证JWT令牌
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 验证JWT令牌并返回用户信息
     * 
     * @param token JWT令牌
     * @return 用户信息，如果令牌无效则返回空
     */
    Optional<UserDTO> validateTokenAndGetUser(String token);
    
    /**
     * 验证Authorization头部
     * 
     * @param authorizationHeader Authorization头部值
     * @return 用户信息，如果验证失败则返回空
     */
    Optional<UserDTO> validateAuthorizationHeader(String authorizationHeader);
    
    /**
     * 刷新JWT令牌
     * 
     * @param token 原JWT令牌
     * @return 新的认证响应
     * @throws IllegalArgumentException 当令牌无效时
     */
    AuthResponse refreshToken(String token);
    
    /**
     * 用户登出
     * 
     * @param token JWT令牌
     * @return 登出是否成功
     */
    boolean logout(String token);
    
    /**
     * 检查JWT令牌是否即将过期
     * 
     * @param token JWT令牌
     * @return 是否即将过期
     */
    boolean isTokenExpiringSoon(String token);
    
    /**
     * 获取JWT令牌的剩余有效时间
     * 
     * @param token JWT令牌
     * @return 剩余有效时间（毫秒）
     */
    Long getTokenRemainingTime(String token);
    
    /**
     * 检查用户是否有指定角色
     * 
     * @param token JWT令牌
     * @param requiredRole 需要的角色
     * @return 是否有权限
     */
    boolean hasRole(String token, String requiredRole);
    
    /**
     * 检查用户是否为管理员
     * 
     * @param token JWT令牌
     * @return 是否为管理员
     */
    boolean isAdmin(String token);
    
    /**
     * 获取当前认证用户信息
     * 
     * @param token JWT令牌
     * @return 用户信息，如果令牌无效则返回空
     */
    Optional<UserDTO> getCurrentUser(String token);
}
