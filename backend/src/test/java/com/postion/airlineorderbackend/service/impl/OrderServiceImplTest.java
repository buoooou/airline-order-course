package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.Order.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.dto.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenExists() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setOrderNumber("ORD-123");
        mockOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        mockOrder.setAmount(new BigDecimal("100.00"));
        mockOrder.setCreationDate(LocalDateTime.now());
        mockOrder.setUserId(1L);
        mockOrder.setFlightId(1L);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // Act
        OrderDto result = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void payOrder_ShouldUpdateStatus_WhenPendingPayment() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.payOrder(orderId);

        // Assert
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(mockOrder);
        assertEquals(OrderStatus.PAID, mockOrder.getStatus());
    }

    @Test
    void cancelOrder_ShouldUpdateStatus_WhenNotCompletedOrCancelled() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.cancelOrder(orderId);

        // Assert
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(mockOrder);
        assertEquals(OrderStatus.CANCELLED, mockOrder.getStatus());
    }
}