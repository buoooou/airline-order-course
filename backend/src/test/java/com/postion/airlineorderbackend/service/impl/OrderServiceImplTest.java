package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
 * OrderServiceImpl的集成测试类
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

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
        when(orderRepository.findAll()).thenReturn(testOrders);

        // When
        List<Order> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetCurrentUserOrders() {
        // Given
        when(orderRepository.findByUserId(testUser.getId())).thenReturn(testOrders);

        // When
        List<Order> result = orderService.getCurrentUserOrders(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository, times(1)).findByUserId(testUser.getId());
    }

    @Test
    void testGetOrderById_Success() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.getOrderById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(testOrder, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.getOrderById(orderId);
        });
        assertEquals("ORDER_NOT_FOUND", exception.getErrorCode());
        assertEquals("订单未找到", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testCreateOrder() {
        // Given
        Order newOrder = new Order();
        newOrder.setAmount(new BigDecimal("200.00"));
        newOrder.setUser(testUser);

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order result = orderService.createOrder(newOrder);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING_PAYMENT, newOrder.getStatus());
        assertNotNull(newOrder.getCreationDate());
        verify(orderRepository, times(1)).save(newOrder);
    }

    @Test
    void testPayOrder_Success() {
        // Given
        Long orderId = 1L;
        Order pendingOrder = new Order();
        pendingOrder.setId(orderId);
        pendingOrder.setStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);

        // When
        Order result = orderService.payOrder(orderId);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PAID, result.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(pendingOrder);
    }

    @Test
    void testPayOrder_CannotPay() {
        // Given
        Long orderId = 1L;
        Order paidOrder = new Order();
        paidOrder.setId(orderId);
        paidOrder.setStatus(OrderStatus.PAID);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(paidOrder));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.payOrder(orderId);
        });
        assertEquals("ORDER_CANNOT_PAY", exception.getErrorCode());
        assertEquals("订单无法支付", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Success() {
        // Given
        Long orderId = 1L;
        Order pendingOrder = new Order();
        pendingOrder.setId(orderId);
        pendingOrder.setStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);

        // When
        Order result = orderService.cancelOrder(orderId);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(pendingOrder);
    }

    @Test
    void testCancelOrder_CannotCancel_Ticketed() {
        // Given
        Long orderId = 1L;
        Order ticketedOrder = new Order();
        ticketedOrder.setId(orderId);
        ticketedOrder.setStatus(OrderStatus.TICKETED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(ticketedOrder));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(orderId);
        });
        assertEquals("ORDER_CANNOT_CANCEL", exception.getErrorCode());
        assertEquals("订单无法取消", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_CannotCancel_AlreadyCancelled() {
        // Given
        Long orderId = 1L;
        Order cancelledOrder = new Order();
        cancelledOrder.setId(orderId);
        cancelledOrder.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(cancelledOrder));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(orderId);
        });
        assertEquals("ORDER_CANNOT_CANCEL", exception.getErrorCode());
        assertEquals("订单无法取消", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testRetryTicketing_Success() {
        // Given
        Long orderId = 1L;
        Order failedOrder = new Order();
        failedOrder.setId(orderId);
        failedOrder.setStatus(OrderStatus.TICKETING_FAILED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(failedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(failedOrder);

        // When
        Order result = orderService.retryTicketing(orderId);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.TICKETING_IN_PROGRESS, result.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(failedOrder);
    }

    @Test
    void testRetryTicketing_CannotRetry() {
        // Given
        Long orderId = 1L;
        Order pendingOrder = new Order();
        pendingOrder.setId(orderId);
        pendingOrder.setStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.retryTicketing(orderId);
        });
        assertEquals("ORDER_CANNOT_RETRY", exception.getErrorCode());
        assertEquals("订单无法重试出票", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrdersByStatus() {
        // Given
        OrderStatus status = OrderStatus.PENDING_PAYMENT;
        when(orderRepository.findByStatus(status)).thenReturn(testOrders);

        // When
        List<Order> result = orderService.getOrdersByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository, times(1)).findByStatus(status);
    }

    @Test
    void testUpdateOrder() {
        // Given
        Order orderToUpdate = new Order();
        orderToUpdate.setId(1L);
        orderToUpdate.setStatus(OrderStatus.PAID);

        when(orderRepository.save(orderToUpdate)).thenReturn(orderToUpdate);

        // When
        Order result = orderService.updateOrder(orderToUpdate);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PAID, result.getStatus());
        verify(orderRepository, times(1)).save(orderToUpdate);
    }

    @Test
    void testDeleteOrder() {
        // Given
        Long orderId = 1L;
        doNothing().when(orderRepository).deleteById(orderId);

        // When
        orderService.deleteOrder(orderId);

        // Then
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void testOrderStatusTransitions() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When - 测试状态转换
        Order paidOrder = orderService.payOrder(1L);
        assertEquals(OrderStatus.PAID, paidOrder.getStatus());

        // 重置状态用于下一个测试
        order.setStatus(OrderStatus.TICKETING_FAILED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order retryOrder = orderService.retryTicketing(1L);
        assertEquals(OrderStatus.TICKETING_IN_PROGRESS, retryOrder.getStatus());
    }
} 