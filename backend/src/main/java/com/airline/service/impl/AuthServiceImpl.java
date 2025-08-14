package com.airline.service.impl;

import com.airline.dto.UserLoginDto;
import com.airline.security.JwtTokenProvider;
import com.airline.service.AuthService;
import com.airline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final Long jwtExpirationInMs;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UserService userService,
                          @Value("${app.jwt.expiration}") Long jwtExpirationInMs) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    @Override
    public AuthResponse login(UserLoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsernameOrEmail(),
                            loginDto.getPassword()
                    )
            );

            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            userService.updateLastLogin(loginDto.getUsernameOrEmail());

            return new AuthResponse(accessToken, refreshToken, jwtExpirationInMs);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("用户名/邮箱或密码错误");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("无效的刷新令牌");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        
        return userService.getUserByUsername(username)
                .map(userDto -> {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            username, null, null);
                    
                    String newAccessToken = tokenProvider.generateToken(authentication);
                    String newRefreshToken = tokenProvider.generateRefreshToken(authentication);
                    
                    return new AuthResponse(newAccessToken, newRefreshToken, jwtExpirationInMs);
                })
                .orElseThrow(() -> new BadCredentialsException("用户不存在"));
    }

    @Override
    public void logout(String token) {
    }
}