package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OrderService接口的单元测试类
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderService orderService;

    private User testUser;
    private Order testOrder;
    private List<Order> testOrders;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");

        // 创建测试订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD-001");
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrder.setAmount(new BigDecimal("100.00"));
        testOrder.setCreationDate(LocalDateTime.now());
        testOrder.setUser(testUser);

        // 创建测试订单列表
        testOrders = Arrays.asList(testOrder);
    }

    @Test
    void testGetAllOrders() {
        // Given
        when(orderService.getAllOrders()).thenReturn(testOrders);

        // When
        List<Order> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetCurrentUserOrders() {
        // Given
        when(orderService.getCurrentUserOrders(testUser)).thenReturn(testOrders);

        // When
        List<Order> result = orderService.getCurrentUserOrders(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderService, times(1)).getCurrentUserOrders(testUser);
    }

    @Test
    void testGetOrderById_Success() {
        // Given
        Long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(testOrder);

        // When
        Order result = orderService.getOrderById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(testOrder, result);
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Given
        Long orderId = 999L;
        when(orderService.getOrderById(orderId))
                .thenThrow(new BusinessException("ORDER_NOT_FOUND", "订单未找到"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.getOrderById(orderId);
        });
        assertEquals("ORDER_NOT_FOUND", exception.getErrorCode());
        assertEquals("订单未找到", exception.getMessage());
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testCreateOrder() {
        // Given
        Order newOrder = new Order();
        newOrder.setAmount(new BigDecimal("200.00"));
        newOrder.setUser(testUser);

        when(orderService.createOrder(newOrder)).thenReturn(testOrder);

        // When
        Order result = orderService.createOrder(newOrder);

        // Then
        assertNotNull(result);
        assertEquals(testOrder, result);
        verify(orderService, times(1)).createOrder(newOrder);
    }

    @Test
    void testPayOrder_Success() {
        // Given
        Long orderId = 1L;
        Order paidOrder = new Order();
        paidOrder.setId(orderId);
        paidOrder.setStatus(OrderStatus.PAID);

        when(orderService.payOrder(orderId)).thenReturn(paidOrder);

        // When
        Order result = orderService.payOrder(orderId);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PAID, result.getStatus());
        verify(orderService, times(1)).payOrder(orderId);
    }

    @Test
    void testPayOrder_CannotPay() {
        // Given
        Long orderId = 1L;
        when(orderService.payOrder(orderId))
                .thenThrow(new BusinessException("ORDER_CANNOT_PAY", "订单无法支付"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.payOrder(orderId);
        });
        assertEquals("ORDER_CANNOT_PAY", exception.getErrorCode());
        assertEquals("订单无法支付", exception.getMessage());
        verify(orderService, times(1)).payOrder(orderId);
    }

    @Test
    void testCancelOrder_Success() {
        // Given
        Long orderId = 1L;
        Order cancelledOrder = new Order();
        cancelledOrder.setId(orderId);
        cancelledOrder.setStatus(OrderStatus.CANCELLED);

        when(orderService.cancelOrder(orderId)).thenReturn(cancelledOrder);

        // When
        Order result = orderService.cancelOrder(orderId);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void testCancelOrder_CannotCancel() {
        // Given
        Long orderId = 1L;
        when(orderService.cancelOrder(orderId))
                .thenThrow(new BusinessException("ORDER_CANNOT_CANCEL", "订单无法取消"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(orderId);
        });
        assertEquals("ORDER_CANNOT_CANCEL", exception.getErrorCode());
        assertEquals("订单无法取消", exception.getMessage());
        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void testRetryTicketing_Success() {
        // Given
        Long orderId = 1L;
        Order retryOrder = new Order();
        retryOrder.setId(orderId);
        retryOrder.setStatus(OrderStatus.TICKETING_IN_PROGRESS);

        when(orderService.retryTicketing(orderId)).thenReturn(retryOrder);

        // When
        Order result = orderService.retryTicketing(orderId);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.TICKETING_IN_PROGRESS, result.getStatus());
        verify(orderService, times(1)).retryTicketing(orderId);
    }

    @Test
    void testRetryTicketing_CannotRetry() {
        // Given
        Long orderId = 1L;
        when(orderService.retryTicketing(orderId))
                .thenThrow(new BusinessException("ORDER_CANNOT_RETRY", "订单无法重试出票"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.retryTicketing(orderId);
        });
        assertEquals("ORDER_CANNOT_RETRY", exception.getErrorCode());
        assertEquals("订单无法重试出票", exception.getMessage());
        verify(orderService, times(1)).retryTicketing(orderId);
    }

    @Test
    void testGetOrdersByStatus() {
        // Given
        OrderStatus status = OrderStatus.PENDING_PAYMENT;
        when(orderService.getOrdersByStatus(status)).thenReturn(testOrders);

        // When
        List<Order> result = orderService.getOrdersByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderService, times(1)).getOrdersByStatus(status);
    }

    @Test
    void testUpdateOrder() {
        // Given
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus(OrderStatus.PAID);

        when(orderService.updateOrder(updatedOrder)).thenReturn(updatedOrder);

        // When
        Order result = orderService.updateOrder(updatedOrder);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PAID, result.getStatus());
        verify(orderService, times(1)).updateOrder(updatedOrder);
    }

    @Test
    void testDeleteOrder() {
        // Given
        Long orderId = 1L;
        doNothing().when(orderService).deleteOrder(orderId);

        // When
        orderService.deleteOrder(orderId);

        // Then
        verify(orderService, times(1)).deleteOrder(orderId);
    }
} 