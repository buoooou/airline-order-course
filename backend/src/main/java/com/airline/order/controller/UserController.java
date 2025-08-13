package com.airline.order.controller;

import com.airline.order.dto.ApiResponse;
import com.airline.order.dto.UserDTO;
import com.airline.order.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户相关操作
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public ApiResponse<UserDTO> getUserInfo(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return ApiResponse.success("获取用户信息成功", userDTO);
    }
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        String message = available ? "用户名可用" : "用户名已存在";
        return ApiResponse.success(message, available);
    }
}
