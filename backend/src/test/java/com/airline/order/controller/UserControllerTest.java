package com.airline.order.controller;

import com.airline.order.dto.ApiResponse;
import com.airline.order.dto.UserDTO;
import com.airline.order.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
                .andExpect(jsonPath("$.message").value("获取用户信息成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("USER"));

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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"))
                .andExpect(jsonPath("$.data").isEmpty());

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
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户名可用"))
                .andExpect(jsonPath("$.data").value(true));

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
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户名已存在"))
                .andExpect(jsonPath("$.data").value(false));

        verify(userService, times(1)).isUsernameAvailable(username);
    }
}