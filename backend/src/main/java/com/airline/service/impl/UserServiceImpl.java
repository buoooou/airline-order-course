package com.airline.service.impl;

import com.airline.dto.UserDto;
import com.airline.dto.UserRegistrationDto;
import com.airline.entity.User;
import com.airline.exception.ResourceNotFoundException;
import com.airline.exception.ValidationException;
import com.airline.mapper.UserMapper;
import com.airline.repository.UserRepository;
import com.airline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        if (existsByUsername(registrationDto.getUsername())) {
            throw new ValidationException("用户名已存在");
        }
        if (existsByEmail(registrationDto.getEmail())) {
            throw new ValidationException("邮箱已存在");
        }

        User user = userMapper.fromRegistrationDto(registrationDto);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersByRoleAndStatus(User.Role role, User.Status status, Pageable pageable) {
        return userRepository.findByRoleAndStatus(role, status, pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(String keyword, Pageable pageable) {
        return userRepository.findByKeyword(keyword, pageable)
                .map(userMapper::toDto);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (!user.getUsername().equals(userDto.getUsername()) && existsByUsername(userDto.getUsername())) {
            throw new ValidationException("用户名已存在");
        }
        if (!user.getEmail().equals(userDto.getEmail()) && existsByEmail(userDto.getEmail())) {
            throw new ValidationException("邮箱已存在");
        }

        userMapper.updateUserFromDto(userDto, user);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    @Override
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                });
    }

    @Override
    public UserDto changeUserStatus(Long id, User.Status status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        user.setStatus(status);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}