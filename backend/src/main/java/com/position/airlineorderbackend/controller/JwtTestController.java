package com.position.airlineorderbackend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jwt-test")
public class JwtTestController {

    /**
     * 测试需要认证的接口
     */
    @GetMapping("/authenticated")
    public String authenticatedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        return "认证成功！当前用户: " + userDetails.getUsername() + 
               ", 权限: " + userDetails.getAuthorities();
    }

    /**
     * 测试需要ADMIN权限的接口
     */
    @GetMapping("/admin-only")
    public String adminOnlyEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        return "管理员接口访问成功！当前用户: " + userDetails.getUsername();
    }

    /**
     * 测试需要USER权限的接口
     */
    @GetMapping("/user-only")
    public String userOnlyEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        return "用户接口访问成功！当前用户: " + userDetails.getUsername();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return "当前用户: " + userDetails.getUsername() + 
                   ", 权限: " + userDetails.getAuthorities();
        }
        return "未认证用户";
    }
} 