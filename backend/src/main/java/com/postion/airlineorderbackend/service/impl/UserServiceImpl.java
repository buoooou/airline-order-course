package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.*;
import com.postion.airlineorderbackend.service.UserService;
import com.postion.airlineorderbackend.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户不存在: " + username));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户ID不存在: " + id));
    }

    @Override
    public User createUser(User user) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("USERNAME_EXISTS", "用户名已被占用");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        // 确认用户存在
        findById(user.getId());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
} 