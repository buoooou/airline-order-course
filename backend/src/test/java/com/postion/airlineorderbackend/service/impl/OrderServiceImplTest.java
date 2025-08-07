package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.statemachine.OrderState;
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
        mockOrder.setStatus(OrderState.PENDING_PAYMENT.name());
        mockOrder.setAmount(new BigDecimal("100.00"));
        mockOrder.setCreationDate(LocalDateTime.now());
        mockOrder.setUserId(1L);
        mockOrder.setFlightId(1L);

        OrderDto expectedDto = new OrderDto();
        expectedDto.setId(orderId);
        expectedDto.setOrderNumber("ORD-123");
        expectedDto.setStatus(OrderState.PENDING_PAYMENT.name());
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




}