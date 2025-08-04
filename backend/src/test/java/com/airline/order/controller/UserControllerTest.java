package com.airline.order.controller;

import com.airline.order.dto.UserDTO;
import com.airline.order.dto.UserRegistrationRequest;
import com.airline.order.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // 准备测试数据
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setRole("USER");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setRole("USER");

        // Mock service 方法
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(userDTO);

        // 执行测试
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.role").value("USER"));

        // 验证 service 方法被调用
        verify(userService, times(1)).registerUser(any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterUser_UsernameExists() throws Exception {
        // 准备测试数据
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");
        request.setRole("USER");

        // Mock service 抛出异常
        when(userService.registerUser(any(UserRegistrationRequest.class)))
                .thenThrow(new RuntimeException("用户名已存在"));

        // 执行测试
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        verify(userService, times(1)).registerUser(any(UserRegistrationRequest.class));
    }

    @Test
    void testLogin_Success() throws Exception {
        // 准备测试数据
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password123");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setRole("USER");

        // Mock service 方法
        when(userService.loginUser("testuser", "password123")).thenReturn(userDTO);

        // 执行测试
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"));

        verify(userService, times(1)).loginUser("testuser", "password123");
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // 准备测试数据
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "wrongpassword");

        // Mock service 抛出异常
        when(userService.loginUser("testuser", "wrongpassword"))
                .thenThrow(new RuntimeException("用户名或密码错误"));

        // 执行测试
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        verify(userService, times(1)).loginUser("testuser", "wrongpassword");
    }

    @Test
    void testGetUserInfo_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("testuser");
        userDTO.setRole("USER");

        // Mock service 方法
        when(userService.getUserById(userId)).thenReturn(userDTO);

        // 执行测试
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.role").value("USER"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetUserInfo_UserNotFound() throws Exception {
        // 准备测试数据
        Long userId = 999L;

        // Mock service 抛出异常
        when(userService.getUserById(userId))
                .thenThrow(new RuntimeException("用户不存在"));

        // 执行测试
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testCheckUsername_Available() throws Exception {
        // 准备测试数据
        String username = "newuser";

        // Mock service 方法
        when(userService.isUsernameAvailable(username)).thenReturn(true);

        // 执行测试
        mockMvc.perform(get("/api/users/check-username")
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.message").value("用户名可用"));

        verify(userService, times(1)).isUsernameAvailable(username);
    }

    @Test
    void testCheckUsername_NotAvailable() throws Exception {
        // 准备测试数据
        String username = "existinguser";

        // Mock service 方法
        when(userService.isUsernameAvailable(username)).thenReturn(false);

        // 执行测试
        mockMvc.perform(get("/api/users/check-username")
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        verify(userService, times(1)).isUsernameAvailable(username);
    }
}