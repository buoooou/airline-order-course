package com.position.airlineorderbackend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    private User testUser;
    private Order testOrder;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(100L);
        testUser.setUsername("test");

        LocalDateTime currentTime = LocalDateTime.now();
        testOrder = new Order();
        testOrder.setId(1001L);
        testOrder.setOrderNumber("AA101001");
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrder.setAmount(new BigDecimal("999.99"));
        testOrder.setCreateTime(currentTime);
        testOrder.setUpdateTime(currentTime);
        testOrder.setUser(testUser);
    }

    @Test
    void testGetAllOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        Order testOrder2 = new Order();
        testOrder2.setId(1002L);
        testOrder2.setOrderNumber("AA101002");
        testOrder2.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrder2.setAmount(new BigDecimal("1999.99"));
        testOrder2.setCreateTime(currentTime);
        testOrder2.setUpdateTime(currentTime);
        testOrder2.setUser(testUser);
        List<Order> mockOrders = Arrays.asList(testOrder, testOrder2);
        // when(orderRepository.findByUserId(testUser.getId())).thenReturn(mockOrders);
        given(orderRepository.findByUserId(testUser.getId())).willReturn(mockOrders);

        List<OrderDTO> result = orderService.getAllOrders(testUser.getId());
        assertEquals(2, result.size());
        assertEquals("AA101001", result.get(0).getOrderNumber());
        assertEquals("AA101002", result.get(1).getOrderNumber());

        verify(orderRepository, times(1)).findByUserId(any());
    }

    @Test
    void testGetOrderByOrderNumber() {
        given(orderRepository.findByOrderNumber(any())).willReturn(Optional.of(testOrder));

        OrderDTO result = orderService.getOrderByOrderNumber(testOrder.getOrderNumber());
        assertEquals("AA101001", result.getOrderNumber());
        assertEquals("999.99", result.getAmount().toString());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getStatus());

        verify(orderRepository, times(1)).findByOrderNumber(any());
    }

    @Test
    void testPayOrder() {
        given(orderRepository.findById(any())).willReturn(Optional.of(testOrder));

        OrderDTO result = orderService.payOrder(testUser.getId());
        assertEquals("AA101001", result.getOrderNumber());
        assertEquals("999.99", result.getAmount().toString());
        assertEquals(OrderStatus.PAID, result.getStatus());

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void testCancelOrder() {
        given(orderRepository.findById(any())).willReturn(Optional.of(testOrder));

        OrderDTO result = orderService.cancelOrder(testUser.getId());
        assertEquals("AA101001", result.getOrderNumber());
        assertEquals("999.99", result.getAmount().toString());
        assertEquals(OrderStatus.CANCELED, result.getStatus());

        verify(orderRepository, times(1)).save(any());
    }
}
