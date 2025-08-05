package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        OrderDto mockOrder = new OrderDto();
        mockOrder.setId(1L);
        mockOrder.setOrderNumber("TEST-123");
        mockOrder.setStatus(OrderStatus.PAID);
        mockOrder.setAmount(new BigDecimal("100.00"));
        mockOrder.setCreateionDate(LocalDateTime.now());

        OrderDto.UserDto mockUser = new OrderDto.UserDto();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockOrder.setUser(mockUser);

        mockOrder.setFlightInfo(Map.of("flightNo", "CA1234", "departure", "Beijing"));

        when(orderService.getOrderById(1L)).thenReturn(mockOrder);
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(mockOrder));
    }

    @Test
    public void testGetAllOrders() {
        List<OrderDto> actualOrders = orderController.getAllOrders();
        assertEquals(1, actualOrders.size());
        OrderDto order = actualOrders.get(0);
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
    public void testGetOrderById() {
        OrderDto actualOrder = orderController.getOrderById(1L);
        assertEquals(1L, actualOrder.getId());
        assertEquals("TEST-123", actualOrder.getOrderNumber());
        assertEquals(OrderStatus.PAID, actualOrder.getStatus());
        assertEquals(new BigDecimal("100.00"), actualOrder.getAmount());
        assertNotNull(actualOrder.getCreateionDate());
        assertNotNull(actualOrder.getUser());
        assertEquals(1L, actualOrder.getUser().getId());
        assertEquals("testUser", actualOrder.getUser().getUsername());
        assertNotNull(actualOrder.getFlightInfo());
        assertEquals("CA1234", actualOrder.getFlightInfo().get("flightNo"));
        assertEquals("Beijing", actualOrder.getFlightInfo().get("departure"));
    }
}