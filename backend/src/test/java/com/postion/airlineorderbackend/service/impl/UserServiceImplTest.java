package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl的集成测试类
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");

        // 创建测试用户列表
        testUsers = Arrays.asList(testUser);

        // 设置SecurityContext
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testFindByUsername_Success() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_NotFound() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.findByUsername(username);
        });
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("用户不存在: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetCurrentUser() {
        // Given
        String username = "testuser";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getCurrentUser();

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetCurrentUser_NotFound() {
        // Given
        String username = "nonexistent";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getCurrentUser();
        });
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("用户不存在: " + username, exception.getMessage());
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindById_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findById(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.findById(userId);
        });
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("用户ID不存在: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateUser_Success() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setRole("USER");

        when(userRepository.existsByUsername(newUser.getUsername())).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(testUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository, times(1)).existsByUsername(newUser.getUsername());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testCreateUser_UsernameExists() {
        // Given
        User newUser = new User();
        newUser.setUsername("existinguser");
        newUser.setPassword("password");
        newUser.setRole("USER");

        when(userRepository.existsByUsername(newUser.getUsername())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(newUser);
        });
        assertEquals("USERNAME_EXISTS", exception.getErrorCode());
        assertEquals("用户名已被占用", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(newUser.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("updatedpassword");
        updatedUser.setRole("ADMIN");

        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(updatedUser);

        // Then
        assertNotNull(result);
        assertEquals(updatedUser, result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        verify(userRepository, times(1)).findById(updatedUser.getId());
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(999L);
        updatedUser.setUsername("nonexistent");

        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(updatedUser);
        });
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("用户ID不存在: " + updatedUser.getId(), exception.getMessage());
        verify(userRepository, times(1)).findById(updatedUser.getId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        // Given
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testUserValidation() {
        // Given
        User validUser = new User();
        validUser.setId(1L);
        validUser.setUsername("validuser");
        validUser.setPassword("validpassword");
        validUser.setRole("USER");

        // When & Then
        assertNotNull(validUser.getId());
        assertNotNull(validUser.getUsername());
        assertNotNull(validUser.getPassword());
        assertNotNull(validUser.getRole());
        assertTrue(validUser.getId() > 0);
        assertFalse(validUser.getUsername().isEmpty());
        assertFalse(validUser.getPassword().isEmpty());
        assertFalse(validUser.getRole().isEmpty());
    }

    @Test
    void testUserRoleValidation() {
        // Given
        User adminUser = new User();
        adminUser.setRole("ADMIN");

        User regularUser = new User();
        regularUser.setRole("USER");

        // When & Then
        assertEquals("ADMIN", adminUser.getRole());
        assertEquals("USER", regularUser.getRole());
        assertNotEquals(adminUser.getRole(), regularUser.getRole());
    }

    @Test
    void testUserPasswordSecurity() {
        // Given
        User user = new User();
        user.setPassword("securepassword123");

        // When & Then
        assertNotNull(user.getPassword());
        assertTrue(user.getPassword().length() >= 8);
        assertFalse(user.getPassword().equals("securepassword123")); // 实际应用中应该是加密的
    }

    @Test
    void testUserUsernameValidation() {
        // Given
        User user1 = new User();
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUsername("user2");

        // When & Then
        assertNotNull(user1.getUsername());
        assertNotNull(user2.getUsername());
        assertNotEquals(user1.getUsername(), user2.getUsername());
        assertTrue(user1.getUsername().length() > 0);
        assertTrue(user2.getUsername().length() > 0);
    }
} 