package com.postion.airlineorderbackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.dto.RegisterRequest;
import com.postion.airlineorderbackend.exception.BussinessException;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.UserService;

import java.util.Optional;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 注册逻辑实现
     */
    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
 	   log.info("开始处理注册请求");
        // 1. 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(registerRequest.getUsername());
        if (!existingUser.isEmpty()) {
        	throw new BussinessException(HttpStatus.BAD_REQUEST,"用户名已存在");
        }

        // 3. 构建用户实体
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        // 4. 密码加密（使用BCrypt加密算法）
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole()); // 默认普通用户角色

        // 5. 保存用户信息到数据库
        userRepository.save(user);
  	   log.info("{}注册成功",user.getUsername());
    }
}
    