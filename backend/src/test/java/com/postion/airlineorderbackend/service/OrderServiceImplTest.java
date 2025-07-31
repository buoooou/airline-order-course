package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
     @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;  // ????Service???

    private User testUser;
    private Order testOrder;
    @BeforeEach
    public void setUp() {
        Long userId = 1L;
        testUser = new User();  
        testUser.setId(userId);
        testUser.setUsername("testUser");   

        Long orderId = 10L;
        testOrder = new Order();
        testOrder.setId(orderId);
        testOrder.setOrderNumber("ORD-2023-001");
        testOrder.setStatus(OrderStatus.PAID);
        testOrder.setAmount(new BigDecimal("999.99"));
        testOrder.setCreationDate(LocalDateTime.now());
        testOrder.setUser(testUser);
    }

    @Test
    @DisplayName("Test get all orders")
    public void testGetOrdersASDtoList() {

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

        List<OrderDto> result = orderService.getAllOrders();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-2023-001", result.get(0).getOrderNumber());
        assertEquals("testUser", result.get(0).getUser().getUserName());   

        verify(orderRepository, times(1)).findAll();
    }
    @Test
    @DisplayName("Test get order by ID")
    public void testGetOrdersASDtoByID() {

        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

       
        OrderDto result = orderService.getOrderById(100L);
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("ORD-2023-001", result.getOrderNumber());
        assertEquals("testUser", result.getUser().getUserName());   

        verify(orderRepository, times(1)).findById(100L);
    }
}
