package com.airline.order.service;

import com.airline.order.dto.UserDTO;
import com.airline.order.dto.UserRegistrationRequest;
import com.airline.order.entity.User;
import com.airline.order.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");

        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setUsername("newuser");
        registrationRequest.setPassword("password123");
        registrationRequest.setRole("USER");
    }

    @Test
    void testRegisterUser_Success() {
        // Mock repository 方法
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行测试
        UserDTO result = userService.registerUser(registrationRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("USER", result.getRole());

        // 验证方法调用
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameExists() {
        // Mock repository 方法
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationRequest);
        });

        assertEquals("用户名已存在", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginUser_Success() {
        // Mock repository 和 passwordEncoder 方法
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // 执行测试
        UserDTO result = userService.loginUser("testuser", "password123");

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("USER", result.getRole());

        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }

    @Test
    void testLoginUser_UserNotFound() {
        // Mock repository 方法
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser("nonexistent", "password123");
        });

        assertEquals("用户不存在", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginUser_WrongPassword() {
        // Mock repository 和 passwordEncoder 方法
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser("testuser", "wrongpassword");
        });

        assertEquals("密码错误", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
    }

    @Test
    void testGetUserById_Success() {
        // Mock repository 方法
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 执行测试
        UserDTO result = userService.getUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("USER", result.getRole());

        // 验证方法调用
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Mock repository 方法
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals("用户不存在", exception.getMessage());

        // 验证方法调用
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testIsUsernameAvailable_Available() {
        // Mock repository 方法
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        // 执行测试
        boolean result = userService.isUsernameAvailable("newuser");

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(userRepository, times(1)).existsByUsername("newuser");
    }

    @Test
    void testIsUsernameAvailable_NotAvailable() {
        // Mock repository 方法
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // 执行测试
        boolean result = userService.isUsernameAvailable("existinguser");

        // 验证结果
        assertFalse(result);

        // 验证方法调用
        verify(userRepository, times(1)).existsByUsername("existinguser");
    }

    @Test
    void testGetUsersByRole() {
        // 准备测试数据
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");
        
        List<User> adminUsers = Arrays.asList(adminUser);

        // Mock repository 方法
        when(userRepository.findByRole("ADMIN")).thenReturn(adminUsers);

        // 执行测试
        List<UserDTO> result = userService.getUsersByRole("ADMIN");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsername());
        assertEquals("ADMIN", result.get(0).getRole());

        // 验证方法调用
        verify(userRepository, times(1)).findByRole("ADMIN");
    }

    @Test
    void testGetUserStatistics() {
        // Mock repository 方法
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByRole("ADMIN")).thenReturn(5L);
        when(userRepository.countByRole("USER")).thenReturn(95L);
        when(userRepository.findUsersWithOrders()).thenReturn(Arrays.asList(testUser));

        // 执行测试
        UserService.UserStatistics result = userService.getUserStatistics();

        // 验证结果
        assertNotNull(result);
        assertEquals(100L, result.getTotalUsers());
        assertEquals(5L, result.getAdminUsers());
        assertEquals(95L, result.getRegularUsers());
        assertEquals(1L, result.getUsersWithOrders());

        // 验证方法调用
        verify(userRepository, times(1)).count();
        verify(userRepository, times(1)).countByRole("ADMIN");
        verify(userRepository, times(1)).countByRole("USER");
        verify(userRepository, times(1)).findUsersWithOrders();
    }
}