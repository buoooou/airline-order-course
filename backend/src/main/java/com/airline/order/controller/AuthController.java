package com.airline.order.controller;

import com.airline.order.dto.ApiResponse;
import com.airline.order.dto.JwtResponse;
import com.airline.order.dto.UserDTO;
import com.airline.order.dto.UserRegistrationRequest;
import com.airline.order.security.jwt.JwtUtils;
import com.airline.order.security.services.UserDetailsImpl;
import com.airline.order.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录和注册请求
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录结果，包含JWT令牌
     */
    @PostMapping("/login")
    public ApiResponse<JwtResponse> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserDTO userDTO = userService.getUserByUsername(userDetails.getUsername());
        
        JwtResponse jwtResponse = new JwtResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDTO.getRole()
        );
        
        return ApiResponse.success("登录成功", jwtResponse);
    }
    
    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<UserDTO> registerUser(@RequestBody UserRegistrationRequest request) {
        UserDTO userDTO = userService.registerUser(request);
        return ApiResponse.success("注册成功", userDTO);
    }
}
