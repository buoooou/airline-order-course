package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.Order.OrderStatus;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        // No need for MockitoAnnotations.openMocks with MockitoExtension
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

        OrderDto expectedDto = new OrderDto();
        expectedDto.setId(orderId);
        expectedDto.setOrderNumber("ORD-123");
        expectedDto.setStatus(OrderStatus.PENDING_PAYMENT);
        expectedDto.setAmount(new BigDecimal("100.00"));
        expectedDto.setCreationDate(LocalDateTime.now());
        expectedDto.setUserId(1L);
        expectedDto.setFlightId(1L);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderMapper.toDto(mockOrder)).thenReturn(expectedDto);

        // Act
        OrderDto result = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toDto(mockOrder);
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