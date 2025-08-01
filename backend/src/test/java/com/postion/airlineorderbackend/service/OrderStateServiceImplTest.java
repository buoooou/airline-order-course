package com.postion.airlineorderbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.postion.airlineorderbackend.entity.OrderStatus;

public class OrderStateServiceImplTest {

    private IOrderStateService orderStateService;

    @BeforeEach
    void setUp() {
        orderStateService = new OrderStateServiceImpl(); // 直接测试真实实现
    }

    @Test
    void testValidTransition_PendingToPaid() {
        OrderStatus next = orderStateService.updateStatus(OrderStatus.PENDING_PAYMENT, OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, next);
    }

    @Test
    void testValidTransition_PaidToTicketingInProgress() {
        OrderStatus next = orderStateService.updateStatus(OrderStatus.PAID, OrderStatus.TICKETING_IN_PROGRESS);
        assertEquals(OrderStatus.TICKETING_IN_PROGRESS, next);
    }

    @Test
    void testValidTransition_TicketingInProgressToTicketed() {
        OrderStatus next = orderStateService.updateStatus(OrderStatus.TICKETING_IN_PROGRESS, OrderStatus.TICKETED);
        assertEquals(OrderStatus.TICKETED, next);
    }

    @Test
    void testValidTransition_TicketingInProgressToTicketingFailed() {
        OrderStatus next = orderStateService.updateStatus(OrderStatus.TICKETING_IN_PROGRESS, OrderStatus.TICKETING_FAILED);
        assertEquals(OrderStatus.TICKETING_FAILED, next);
    }

    @Test
    void testInvalidTransition_PendingToTicketed() {
        Exception ex = assertThrows(IllegalStateException.class,
                () -> orderStateService.updateStatus(OrderStatus.PENDING_PAYMENT, OrderStatus.TICKETED));
        assertEquals("Invalid state transition: PENDING_PAYMENT -> TICKETED", ex.getMessage());
    }

    @Test
    void testInvalidTransition_TicketedToPaid() {
        Exception ex = assertThrows(IllegalStateException.class,
                () -> orderStateService.updateStatus(OrderStatus.TICKETED, OrderStatus.PAID));
        assertTrue(ex.getMessage().contains("Invalid state transition"));
    }

    @Test
    void testNoTransitionFromCancelled() {
        Exception ex = assertThrows(IllegalStateException.class,
                () -> orderStateService.updateStatus(OrderStatus.CANCELLED, OrderStatus.PAID));
        assertTrue(ex.getMessage().contains("Invalid state transition"));
    }
}
