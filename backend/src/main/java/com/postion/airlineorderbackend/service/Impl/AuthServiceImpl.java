package com.postion.airlineorderbackend.service.Impl;

import com.postion.airlineorderbackend.dto.LoginRequest;
import com.postion.airlineorderbackend.dto.LoginResponse;
import com.postion.airlineorderbackend.dto.RegisterRequest;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.security.JwtTokenProvider;
import com.postion.airlineorderbackend.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    
    public void register(RegisterRequest request) {
        // 检查用户名冲突
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username.taken");
        }

        // 构建新用户对象
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // 加密后存储
                .role("USER")
                .build();

        // 保存用户
        userRepository.save(user);
    }
    
    public LoginResponse login(LoginRequest request) {    	 
    	User user = userRepository.findByUsername(request.getUsername())
    			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "login.invalid: the user is not exist:"+request.getUsername()));
    	
    	if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "login.invalid: password is not correct.");
    	}
    	
    	String token = jwtTokenProvider.generateToken(user);
    	return new LoginResponse(token, user.getUsername(), user.getRole());
    }
}
