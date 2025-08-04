package com.airline.order.controller;

import com.airline.order.dto.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 * 用于测试JWT认证
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    /**
     * 公共内容
     * 所有用户都可以访问
     * @return 响应信息
     */
    @GetMapping("/all")
    public ApiResponse<String> allAccess() {
        return ApiResponse.success("公共内容");
    }

    /**
     * 用户内容
     * 只有USER角色的用户可以访问
     * @return 响应信息
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<String> userAccess() {
        return ApiResponse.success("用户内容");
    }

    /**
     * 管理员内容
     * 只有ADMIN角色的用户可以访问
     * @return 响应信息
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> adminAccess() {
        return ApiResponse.success("管理员内容");
    }
}