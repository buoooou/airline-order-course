package com.airline.service;

import com.airline.dto.UserDto;
import com.airline.dto.UserRegistrationDto;
import com.airline.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    UserDto registerUser(UserRegistrationDto registrationDto);

    Optional<UserDto> getUserById(Long id);

    Optional<UserDto> getUserByUsername(String username);

    Optional<UserDto> getUserByEmail(String email);

    Page<UserDto> getAllUsers(Pageable pageable);

    Page<UserDto> getUsersByRoleAndStatus(User.Role role, User.Status status, Pageable pageable);

    Page<UserDto> searchUsers(String keyword, Pageable pageable);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long countUsersByRole(User.Role role);

    void updateLastLogin(String username);

    UserDto changeUserStatus(Long id, User.Status status);
}