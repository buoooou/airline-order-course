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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setOrderNumber("ORD12345");
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrder.setAmount(new BigDecimal("25.75"));
        testOrder.setCreationDate(LocalDateTime.now());
        testOrder.setUser(testUser);
    }

    @Test
    @DisplayName("当调用 getAllOrders 时，应返回所有订单的 DTO 列表")
    void shouldReturnAllOrdersAsDtoList() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

        List<OrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD12345", result.get(0).getOrderNumber());
        assertEquals("testuser", result.get(0).getUser().getUsername());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("当使用有效 ID 调用 getOrderById 时，应返回对应的订单 DTO")
    void shouldReturnOrderDtoForValidId() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        OrderDto result = orderService.getOrderById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("ORD12345", result.getOrderNumber());
        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("当使用无效 ID 调用 getOrderById 时，应抛出 RuntimeException")
    void shouldThrowExceptionForInvalidId() {
        Long invalidId = 999L;
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrderById(invalidId));
        assertEquals("Order not found with id: " + invalidId, exception.getMessage());

        verify(orderRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("当支付状态为PENDING_PAYMENT的订单时，应成功更新状态为PAID")
    void shouldPayOrderWhenStatusIsPendingPayment() {
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.payOrder(100L);

        assertNotNull(result);
        assertEquals(OrderStatus.PAID, result.getStatus());
        verify(orderRepository, times(1)).findById(100L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    @DisplayName("当支付状态不是PENDING_PAYMENT的订单时，应抛出异常")
    void shouldThrowExceptionWhenPayNonPendingOrder() {
        testOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.payOrder(100L));
        assertEquals("Order cannot be paid as it's not in PENDING status", exception.getMessage());

        verify(orderRepository, times(1)).findById(100L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("当请求出票状态为PAID的订单时，应成功处理")
    void shouldRequestTicketIssuanceForPaidOrder() {
        testOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        orderService.requestTicketIssuance(100L);

        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("当请求出票状态不是PAID的订单时，应抛出异常")
    void shouldThrowExceptionWhenRequestTicketForNonPaidOrder() {
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.requestTicketIssuance(100L));
        assertEquals("Ticket can only be issued for PAID orders", exception.getMessage());

        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("当取消未完成的订单时，应成功更新状态为CANCELLED")
    void shouldCancelUncompletedOrder() {
        testOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.cancelOrder(100L);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(orderRepository, times(1)).findById(100L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    @DisplayName("当取消已取消的订单时，应抛出异常")
    void shouldThrowExceptionWhenCancelAlreadyCancelledOrder() {
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(100L));
        assertEquals("Order is already cancelled", exception.getMessage());

        verify(orderRepository, times(1)).findById(100L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("当取消已出票的订单时，应抛出异常")
    void shouldThrowExceptionWhenCancelTicketedOrder() {
        testOrder.setStatus(OrderStatus.TICKETED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(100L));
        assertEquals("Completed orders cannot be cancelled", exception.getMessage());

        verify(orderRepository, times(1)).findById(100L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("应允许从PENDING_PAYMENT到PAID的转换")
    void shouldAllowPendingToPaidTransition() {
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.updateStatus(100L, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, result.getStatus());
    }

    @Test
    @DisplayName("应允许从PENDING_PAYMENT到CANCELLED的转换")
    void shouldAllowPendingToCancelledTransition() {
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.updateStatus(100L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    @DisplayName("应拒绝从PENDING_PAYMENT到TICKETED的非法转换")
    void shouldRejectPendingToTicketedTransition() {
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        assertThrows(IllegalStateException.class,
                () -> orderService.updateStatus(100L, OrderStatus.TICKETED));
    }

    @Test
    @DisplayName("应允许从PAID到TICKETING_IN_PROGRESS的转换")
    void shouldAllowPaidToTicketingInProgressTransition() {
        testOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.updateStatus(100L, OrderStatus.TICKETING_IN_PROGRESS);

        assertEquals(OrderStatus.TICKETING_IN_PROGRESS, result.getStatus());
    }

    @Test
    @DisplayName("应允许从TICKETING_IN_PROGRESS到TICKETED的转换")
    void shouldAllowTicketingToTicketedTransition() {
        testOrder.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.updateStatus(100L, OrderStatus.TICKETED);

        assertEquals(OrderStatus.TICKETED, result.getStatus());
    }

    @Test
    @DisplayName("应允许从TICKETING_IN_PROGRESS到TICKETING_FAILED的转换")
    void shouldAllowTicketingToFailedTransition() {
        testOrder.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.updateStatus(100L, OrderStatus.TICKETING_FAILED);

        assertEquals(OrderStatus.TICKETING_FAILED, result.getStatus());
    }

    @Test
    @DisplayName("应允许从TICKETING_FAILED到TICKETING_IN_PROGRESS的转换")
    void shouldAllowFailedToTicketingTransition() {
        testOrder.setStatus(OrderStatus.TICKETING_FAILED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.updateStatus(100L, OrderStatus.TICKETING_IN_PROGRESS);

        assertEquals(OrderStatus.TICKETING_IN_PROGRESS, result.getStatus());
    }

    @Test
    @DisplayName("应允许从TICKETED到CANCELLED的转换")
    void shouldAllowTicketedToCancelledTransition() {
        testOrder.setStatus(OrderStatus.TICKETED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        OrderDto result = orderService.updateStatus(100L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    @DisplayName("应拒绝从CANCELLED到任何状态的转换")
    void shouldRejectAnyTransitionFromCancelled() {
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        assertThrows(IllegalStateException.class,
                () -> orderService.updateStatus(100L, OrderStatus.PAID));
    }

    @Test
    @DisplayName("应拒绝从未知状态的转换")
    void shouldRejectUnknownStatusTransition() {
        // Using reflection to test the default case
        OrderStatus unknownStatus = OrderStatus.valueOf("UNKNOWN");
        testOrder.setStatus(unknownStatus);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateStatus(100L, OrderStatus.PAID));
    }
}