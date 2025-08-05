package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl; // 假设有一个实现类 OrderServiceImpl

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setOrderNumber("TEST-123");
        mockOrder.setStatus(OrderStatus.PAID);
        mockOrder.setAmount(new BigDecimal("100.00"));
        mockOrder.setCreateionDate(LocalDateTime.now());
        mockOrder.setUpdateDate(LocalDateTime.now());

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setRole("ADMIN");
        mockOrder.setUser(mockUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(mockOrder));
    }

    @Test
    public void testGetOrderById() {
        OrderDto order = orderServiceImpl.getOrderById(1L);
        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertEquals("TEST-123", order.getOrderNumber());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(new BigDecimal("100.00"), order.getAmount());
        assertNotNull(order.getCreateionDate());
        assertNotNull(order.getUser());
        assertEquals(1L, order.getUser().getId());
        assertEquals("testUser", order.getUser().getUsername());
        assertNotNull(order.getFlightInfo());
        assertEquals("CA1234", order.getFlightInfo().get("flightNo"));
        assertEquals("Beijing", order.getFlightInfo().get("departure"));
    }

    @Test
    public void testGetAllOrders() {
        List<OrderDto> orders = orderServiceImpl.getAllOrders();
        assertNotNull(orders);
        assertEquals(1, orders.size());
        OrderDto order = orders.get(0);
        assertEquals(1L, order.getId());
        assertEquals("TEST-123", order.getOrderNumber());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(new BigDecimal("100.00"), order.getAmount());
        assertNotNull(order.getCreateionDate());
        assertNotNull(order.getUser());
        assertEquals(1L, order.getUser().getId());
        assertEquals("testUser", order.getUser().getUsername());
        assertNotNull(order.getFlightInfo());
        assertEquals("CA1234", order.getFlightInfo().get("flightNo"));
        assertEquals("Beijing", order.getFlightInfo().get("departure"));
    }
}