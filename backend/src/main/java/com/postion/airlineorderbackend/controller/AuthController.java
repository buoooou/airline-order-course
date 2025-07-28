package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.AuthRequest;
import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.enums.UserRole;
import com.postion.airlineorderbackend.service.AuthService;
import com.postion.airlineorderbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证控制器
 * 提供用户登录、登出、令牌验证等认证相关的REST API
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "认证管理", description = "用户认证相关API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    /**
     * 用户登录
     * @param authRequest 登录请求
     * @return 认证响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名和密码进行登录认证")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("用户登录请求: {}", authRequest.getUsername());
        
        try {
            AuthResponse response = authService.login(authRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (BadCredentialsException e) {
            log.warn("用户登录失败: {}", authRequest.getUsername());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "用户名或密码错误");
            error.put("error", "INVALID_CREDENTIALS");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            
        } catch (Exception e) {
            log.error("用户登录异常: {}", authRequest.getUsername(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "登录失败，请稍后重试");
            error.put("error", "LOGIN_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param role 用户角色（可选，默认为USER）
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "用户名已存在或参数无效"),
            @ApiResponse(responseCode = "500", description = "注册失败")
    })
    public ResponseEntity<?> register(
            @Parameter(description = "用户名", required = true) @RequestParam String username,
            @Parameter(description = "密码", required = true) @RequestParam String password,
            @Parameter(description = "用户角色") @RequestParam(defaultValue = "USER") String role) {
        
        log.info("用户注册请求: {}, 角色: {}", username, role);
        
        try {
            // 验证用户名是否已存在
            if (userService.existsByUsername(username)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "用户名已存在");
                error.put("error", "USERNAME_EXISTS");
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // 解析用户角色
            UserRole userRole;
            try {
                userRole = UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                userRole = UserRole.USER; // 默认为普通用户
            }
            
            // 创建用户
            userService.createUser(username, password, userRole);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("data", Map.of("username", username, "role", userRole.name()));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (Exception e) {
            log.error("用户注册异常: {}", username, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "注册失败: " + e.getMessage());
            error.put("error", "REGISTRATION_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 验证JWT令牌
     * @param authorizationHeader Authorization头部
     * @return 验证结果
     */
    @PostMapping("/validate")
    @Operation(summary = "验证JWT令牌", description = "验证JWT令牌的有效性并返回用户信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "令牌有效"),
            @ApiResponse(responseCode = "401", description = "令牌无效或已过期")
    })
    public ResponseEntity<?> validateToken(
            @Parameter(description = "Authorization头部", required = true) 
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.debug("验证JWT令牌请求");
        
        try {
            Optional<UserDTO> userOpt = authService.validateAuthorizationHeader(authorizationHeader);
            
            if (userOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "令牌有效");
                result.put("data", userOpt.get());
                
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "令牌无效或已过期");
                error.put("error", "INVALID_TOKEN");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
        } catch (Exception e) {
            log.error("令牌验证异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "令牌验证失败");
            error.put("error", "TOKEN_VALIDATION_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 刷新JWT令牌
     * @param authorizationHeader Authorization头部
     * @return 新的认证响应
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新JWT令牌", description = "使用现有令牌获取新的JWT令牌")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "令牌刷新成功"),
            @ApiResponse(responseCode = "401", description = "令牌无效，无法刷新"),
            @ApiResponse(responseCode = "400", description = "刷新失败")
    })
    public ResponseEntity<?> refreshToken(
            @Parameter(description = "Authorization头部", required = true) 
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("刷新JWT令牌请求");
        
        try {
            // 提取令牌
            String token = authorizationHeader.replace("Bearer ", "");
            
            AuthResponse response = authService.refreshToken(token);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "令牌刷新成功");
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("令牌刷新失败: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", "REFRESH_FAILED");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            
        } catch (Exception e) {
            log.error("令牌刷新异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "令牌刷新失败");
            error.put("error", "REFRESH_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 用户登出
     * @param authorizationHeader Authorization头部
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出系统")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登出成功"),
            @ApiResponse(responseCode = "400", description = "登出失败")
    })
    public ResponseEntity<?> logout(
            @Parameter(description = "Authorization头部") 
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        
        log.info("用户登出请求");
        
        try {
            boolean success = true;
            
            if (authorizationHeader != null) {
                String token = authorizationHeader.replace("Bearer ", "");
                success = authService.logout(token);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "登出成功" : "登出失败");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("用户登出异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "登出失败");
            error.put("error", "LOGOUT_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 获取当前用户信息
     * @param authorizationHeader Authorization头部
     * @return 当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "根据JWT令牌获取当前登录用户的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效")
    })
    public ResponseEntity<?> getCurrentUser(
            @Parameter(description = "Authorization头部", required = true) 
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.debug("获取当前用户信息请求");
        
        try {
            Optional<UserDTO> userOpt = authService.validateAuthorizationHeader(authorizationHeader);
            
            if (userOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "获取用户信息成功");
                result.put("data", userOpt.get());
                
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "未认证或令牌无效");
                error.put("error", "UNAUTHORIZED");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
        } catch (Exception e) {
            log.error("获取当前用户信息异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取用户信息失败");
            error.put("error", "GET_USER_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 检查令牌是否即将过期
     * @param authorizationHeader Authorization头部
     * @return 检查结果
     */
    @GetMapping("/token/check")
    @Operation(summary = "检查令牌状态", description = "检查JWT令牌是否即将过期")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "检查成功"),
            @ApiResponse(responseCode = "401", description = "令牌无效")
    })
    public ResponseEntity<?> checkTokenStatus(
            @Parameter(description = "Authorization头部", required = true) 
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.debug("检查令牌状态请求");
        
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            
            boolean isValid = authService.validateToken(token);
            if (!isValid) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "令牌无效");
                error.put("error", "INVALID_TOKEN");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            boolean isExpiringSoon = authService.isTokenExpiringSoon(token);
            Long remainingTime = authService.getTokenRemainingTime(token);
            
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("valid", true);
            tokenInfo.put("expiringSoon", isExpiringSoon);
            tokenInfo.put("remainingTimeMs", remainingTime);
            tokenInfo.put("remainingTimeMinutes", remainingTime / (1000 * 60));
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "令牌状态检查完成");
            result.put("data", tokenInfo);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("检查令牌状态异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "令牌状态检查失败");
            error.put("error", "TOKEN_CHECK_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
