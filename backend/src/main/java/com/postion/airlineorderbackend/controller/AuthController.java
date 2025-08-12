package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.AuthRequest;
import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.dto.RegisterRequest;
import com.postion.airlineorderbackend.service.JwtService;
import com.postion.airlineorderbackend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制层
 *
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    
    private final UserService userService;

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "登录", description = "登录")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request){
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(ApiResponse.success("登录成功,token获取", new AuthResponse(token)));
    }
    

    /**
     * 用户注册接口
     * @param user
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "注册", description = "注册")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegisterRequest registerRequest) {
        // 调用服务层执行注册逻辑
        userService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("用户注册成功", null));
    }
}