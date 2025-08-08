package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("USERNAME_REQUIRED", "用户名不能为空");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户不存在: " + username));
    }

    @Override
    public User getCurrentUser() {
        throw new BusinessException("NOT_IMPLEMENTED", "请通过安全上下文实现获取当前用户逻辑");
    }

    @Override
    public User findById(Long id) {
        if (id == null) {
            throw new BusinessException("USER_ID_REQUIRED", "用户ID不能为空");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "用户ID不存在: " + id));
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new BusinessException("USER_REQUIRED", "用户信息不能为空");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BusinessException("USERNAME_REQUIRED", "用户名不能为空");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("USERNAME_EXISTS", "用户名已被占用");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new BusinessException("USER_ID_REQUIRED", "用户ID不能为空");
        }
        findById(user.getId());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null) {
            throw new BusinessException("USER_ID_REQUIRED", "用户ID不能为空");
        }
        findById(id);
        userRepository.deleteById(id);
    }
} 