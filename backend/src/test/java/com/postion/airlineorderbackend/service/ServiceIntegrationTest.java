package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 服务层集成测试类
 * 测试真实的数据库交互和业务逻辑
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("integrationtest");
        testUser.setPassword("password");
        testUser.setRole("USER");

        // 保存用户
        testUser = userService.createUser(testUser);

        // 创建测试订单
        testOrder = new Order();
        testOrder.setAmount(new BigDecimal("100.00"));
        testOrder.setUser(testUser);
    }

    @Test
    void testCreateAndRetrieveOrder() {
        // Given - 在setUp中已创建

        // When
        Order createdOrder = orderService.createOrder(testOrder);
        Order retrievedOrder = orderService.getOrderById(createdOrder.getId());

        // Then
        assertNotNull(createdOrder);
        assertNotNull(retrievedOrder);
        assertEquals(createdOrder.getId(), retrievedOrder.getId());
        assertEquals(OrderStatus.PENDING_PAYMENT, createdOrder.getStatus());
        assertEquals(testUser.getId(), createdOrder.getUser().getId());
    }

    @Test
    void testOrderPaymentFlow() {
        // Given
        Order order = orderService.createOrder(testOrder);

        // When - 支付订单
        Order paidOrder = orderService.payOrder(order.getId());

        // Then
        assertEquals(OrderStatus.PAID, paidOrder.getStatus());
        assertEquals(order.getId(), paidOrder.getId());
    }

    @Test
    void testOrderCancellationFlow() {
        // Given
        Order order = orderService.createOrder(testOrder);

        // When - 取消订单
        Order cancelledOrder = orderService.cancelOrder(order.getId());

        // Then
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
        assertEquals(order.getId(), cancelledOrder.getId());
    }

    @Test
    void testGetCurrentUserOrders() {
        // Given
        orderService.createOrder(testOrder);

        // When
        List<Order> userOrders = orderService.getCurrentUserOrders(testUser);

        // Then
        assertNotNull(userOrders);
        assertFalse(userOrders.isEmpty());
        userOrders.forEach(order -> assertEquals(testUser.getId(), order.getUser().getId()));
    }

    @Test
    void testGetOrdersByStatus() {
        // Given
        orderService.createOrder(testOrder);

        // When
        List<Order> pendingOrders = orderService.getOrdersByStatus(OrderStatus.PENDING_PAYMENT);

        // Then
        assertNotNull(pendingOrders);
        assertFalse(pendingOrders.isEmpty());
        pendingOrders.forEach(order -> assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus()));
    }

    @Test
    void testOrderNotFound() {
        // When & Then
        assertThrows(BusinessException.class, () -> {
            orderService.getOrderById(999L);
        });
    }

    @Test
    void testCannotPayAlreadyPaidOrder() {
        // Given
        Order order = orderService.createOrder(testOrder);
        orderService.payOrder(order.getId());

        // When & Then
        assertThrows(BusinessException.class, () -> {
            orderService.payOrder(order.getId());
        });
    }

    @Test
    void testCannotCancelTicketedOrder() {
        // Given
        Order order = orderService.createOrder(testOrder);
        // 模拟订单已出票
        order.setStatus(OrderStatus.TICKETED);
        orderService.updateOrder(order);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(order.getId());
        });
    }

    @Test
    void testUserCreationAndRetrieval() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setRole("USER");

        // When
        User createdUser = userService.createUser(newUser);
        User retrievedUser = userService.findByUsername(createdUser.getUsername());

        // Then
        assertNotNull(createdUser);
        assertNotNull(retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals("newuser", retrievedUser.getUsername());
    }

    @Test
    void testUserNotFound() {
        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.findByUsername("nonexistent");
        });
    }

    @Test
    void testDuplicateUsername() {
        // Given
        User user1 = new User();
        user1.setUsername("duplicate");
        user1.setPassword("password");
        user1.setRole("USER");

        User user2 = new User();
        user2.setUsername("duplicate");
        user2.setPassword("password");
        user2.setRole("USER");

        // When
        userService.createUser(user1);

        // Then
        assertThrows(BusinessException.class, () -> {
            userService.createUser(user2);
        });
    }

    @Test
    void testOrderStatusTransitions() {
        // Given
        Order order = orderService.createOrder(testOrder);
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());

        // When - 支付
        Order paidOrder = orderService.payOrder(order.getId());
        assertEquals(OrderStatus.PAID, paidOrder.getStatus());

        // 模拟出票失败
        paidOrder.setStatus(OrderStatus.TICKETING_FAILED);
        orderService.updateOrder(paidOrder);

        // When - 重试出票
        Order retryOrder = orderService.retryTicketing(paidOrder.getId());
        assertEquals(OrderStatus.TICKETING_IN_PROGRESS, retryOrder.getStatus());
    }

    @Test
    void testOrderAmountValidation() {
        // Given
        Order order = new Order();
        order.setAmount(new BigDecimal("0.00")); // 无效金额
        order.setUser(testUser);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(order);
        });
    }

    @Test
    void testUserRoleValidation() {
        // Given
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("password");
        adminUser.setRole("ADMIN");

        // When
        User createdAdmin = userService.createUser(adminUser);

        // Then
        assertEquals("ADMIN", createdAdmin.getRole());
        assertNotEquals("USER", createdAdmin.getRole());
    }
} 