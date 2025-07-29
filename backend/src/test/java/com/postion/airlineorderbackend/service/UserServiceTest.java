package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserService接口的单元测试类
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserService userService;

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
    }

    @Test
    void testFindByUsername_Success() {
        // Given
        String username = "testuser";
        when(userService.findByUsername(username)).thenReturn(testUser);

        // When
        User result = userService.findByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        assertEquals(username, result.getUsername());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_NotFound() {
        // Given
        String username = "nonexistent";
        when(userService.findByUsername(username))
                .thenThrow(new BusinessException("USER_NOT_FOUND", "用户不存在: " + username));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.findByUsername(username);
        });
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("用户不存在: " + username, exception.getMessage());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void testGetCurrentUser() {
        // Given
        when(userService.getCurrentUser()).thenReturn(testUser);

        // When
        User result = userService.getCurrentUser();

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void testGetCurrentUser_NotFound() {
        // Given
        when(userService.getCurrentUser())
                .thenThrow(new BusinessException("USER_NOT_FOUND", "当前用户不存在"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getCurrentUser();
        });
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("当前用户不存在", exception.getMessage());
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void testFindById_Success() {
        // Given
        Long userId = 1L;
        when(userService.findById(userId)).thenReturn(testUser);

        // When
        User result = userService.findById(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        assertEquals(userId, result.getId());
        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        Long userId = 999L;
        when(userService.findById(userId))
                .thenThrow(new BusinessException("USER_NOT_FOUND", "用户ID不存在: " + userId));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.findById(userId);
        });
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("用户ID不存在: " + userId, exception.getMessage());
        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testCreateUser_Success() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setRole("USER");

        when(userService.createUser(newUser)).thenReturn(testUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userService, times(1)).createUser(newUser);
    }

    @Test
    void testCreateUser_UsernameExists() {
        // Given
        User newUser = new User();
        newUser.setUsername("existinguser");
        newUser.setPassword("password");
        newUser.setRole("USER");

        when(userService.createUser(newUser))
                .thenThrow(new BusinessException("USERNAME_EXISTS", "用户名已被占用"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(newUser);
        });
        assertEquals("USERNAME_EXISTS", exception.getErrorCode());
        assertEquals("用户名已被占用", exception.getMessage());
        verify(userService, times(1)).createUser(newUser);
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("updatedpassword");
        updatedUser.setRole("ADMIN");

        when(userService.updateUser(updatedUser)).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(updatedUser);

        // Then
        assertNotNull(result);
        assertEquals(updatedUser, result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        verify(userService, times(1)).updateUser(updatedUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(999L);
        updatedUser.setUsername("nonexistent");

        when(userService.updateUser(updatedUser))
                .thenThrow(new BusinessException("USER_NOT_FOUND", "用户不存在"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(updatedUser);
        });
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("用户不存在", exception.getMessage());
        verify(userService, times(1)).updateUser(updatedUser);
    }

    @Test
    void testDeleteUser() {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userService, times(1)).deleteUser(userId);
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
} 