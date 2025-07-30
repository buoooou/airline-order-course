package com.position.airlineorderbackend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postion.airlineorderbackend.Exception.AirlineBusinessException;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private User testUser;
    private Order testOrder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        LocalDateTime currentTime = LocalDateTime.now();
        testUser = new User();
        testUser.setId(100L);
        testUser.setUsername("testname");
        testUser.setPassword("test123");
        testUser.setRole("ADMIN");
        testUser.setCreateTime(currentTime);
        testUser.setUpdateTime(currentTime);

        testOrder = new Order();
        testOrder.setId(1001L);
        testOrder.setOrderNumber("AA101001");
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrder.setAmount(new BigDecimal("999.99"));
        testOrder.setCreateTime(currentTime);
        testOrder.setUpdateTime(currentTime);
        testOrder.setUser(testUser);
    }

    @Test
    void testGetUserByUsername_exist() {
        given(userRepository.findByUsername("testname")).willReturn(Optional.of(testUser));

        UserDTO result = userService.getUserByUsername("testname");
        assertEquals("testname", result.getUsername());
        assertEquals("test123", result.getPassword());
        assertEquals("ADMIN", result.getRole());

        verify(userRepository, times(1)).findByUsername(any());
    }

    @Test
    void testGetUserByUsername_notexist() {
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        RuntimeException exception = assertThrows(AirlineBusinessException.class, () -> userService.getUserByUsername(testUser.getUsername()));

        assertEquals("The user does not exist.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
