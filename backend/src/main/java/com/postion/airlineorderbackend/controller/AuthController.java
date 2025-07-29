package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.service.UserService;
import com.postion.airlineorderbackend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户注册、登录、登出等认证相关接口")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "用户注册", description = "注册新用户账户")
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @Parameter(description = "用户注册信息", required = true) @Valid @RequestBody User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User registeredUser = userService.createUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @Operation(summary = "用户登录", description = "用户登录并获取JWT令牌")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "登录信息", required = true) @Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 查找用户
            User user = userService.findByUsername(loginRequest.getUsername());
            
            // 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("用户名或密码错误"));
            }
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            
            // 创建响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUser(user);
            response.setMessage("登录成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("用户名或密码错误"));
        }
    }

    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Parameter(description = "刷新令牌请求", required = true) @Valid @RequestBody RefreshTokenRequest request) {
        // 这里可以实现令牌刷新逻辑
        return ResponseEntity.ok(new LoginResponse());
    }

    @Operation(summary = "用户登出", description = "用户登出系统")
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        LogoutResponse response = new LogoutResponse();
        response.setMessage("登出成功");
        return ResponseEntity.ok(response);
    }

    private LoginResponse createErrorResponse(String message) {
        LoginResponse response = new LoginResponse();
        response.setMessage(message);
        return response;
    }

    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 登录响应DTO
     */
    public static class LoginResponse {
        private String token;
        private User user;
        private String message;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * 刷新令牌请求DTO
     */
    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    /**
     * 登出响应DTO
     */
    public static class LogoutResponse {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
} 