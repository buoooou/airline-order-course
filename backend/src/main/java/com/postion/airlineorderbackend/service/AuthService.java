package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public String register(String username, String email, String password) {
        System.out.println("注册请求参数 - 用户名: " + username + ", 邮箱: " + email);
        if (userRepository.findByUsername(username) != null) {
            System.out.println("用户名已存在: " + username);
            return "Username already exists";
        }

        if (userRepository.findByEmail(email) != null) {
            System.out.println("邮箱已存在: " + email);
            return "Email already exists";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        System.out.println("创建用户并保存到数据库: " + username);
        userRepository.save(user);
        System.out.println("用户注册成功: " + username);
        return "User registered successfully";
    }

    public AuthResponse login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            password
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return new AuthResponse(token);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}