package com.position.airlineorderbackend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.position.airlineorderbackend.dto.OrderDto;
import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.model.OrderStatus;
import com.position.airlineorderbackend.repo.OrderRepository;
import com.position.airlineorderbackend.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }

    @Test
    void shouldReturnAllOrders() {
        // arrange
        List<Order> mockOrders = Arrays.asList(
            createMockOrder(1L, OrderStatus.PENDING_PAYMENT),
            createMockOrder(2L, OrderStatus.PAID)
        );
        when(orderRepository.findAll()).thenReturn(mockOrders);

        // act
        List<OrderDto> result = orderService.getAllOrders();

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void shouldReturnOrderById_WhenOrderExists() {
        // arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId, OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // act
        OrderDto result = orderService.getOrderById(orderId);

        // assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldReturnNull_WhenOrderNotExists() {
        // arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // act
        OrderDto result = orderService.getOrderById(orderId);

        // assert
        assertNull(result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldPayOrder_WhenOrderExists() {
        // arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId, OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // act
        String result = orderService.payOrder(orderId);

        // assert
        assertEquals("订单支付成功", result);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldStartTicketing_WhenOrderExists() {
        // arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId, OrderStatus.PAID);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // act
        String result = orderService.startTicketing(orderId);

        // assert
        assertEquals("开始出票处理", result);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldCompleteTicketing_WhenOrderExists() {
        // arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId, OrderStatus.TICKETING_IN_PROGRESS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // act
        String result = orderService.completeTicketing(orderId);

        // assert
        assertEquals("出票完成", result);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldFailTicketing_WhenOrderExists() {
        // arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId, OrderStatus.TICKETING_IN_PROGRESS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // act
        String result = orderService.failTicketing(orderId);

        // assert
        assertEquals("出票失败状态已更新", result);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldCancelOrder_WhenOrderExists() {
        // arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId, OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // act
        String result = orderService.cancelOrder(orderId);

        // assert
        assertEquals("订单已取消", result);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldRetryPayment_WhenOrderExists() {
        // arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId, OrderStatus.TICKETING_FAILED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // act
        String result = orderService.retryPayment(orderId);

        // assert
        assertEquals("重新支付成功，可重新出票", result);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldRetryTicketing_WhenOrderExists() {
        // arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId, OrderStatus.TICKETING_FAILED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // act
        String result = orderService.retryTicketing(orderId);

        // assert
        assertEquals("重新出票处理中", result);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    private OrderDto createMockOrderDto(Long id) {
        OrderDto dto = new OrderDto();
        dto.setId(id);
        dto.setOrderNumber("ORDER-" + id);
        dto.setStatus(OrderStatus.PENDING_PAYMENT);
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setCreatedDate(LocalDateTime.now());
        dto.setUserId(1L);
        dto.setFlightInfoId(1L);
        return dto;
    }

    private Order createMockOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setOrderNumber("ORDER-" + id);
        order.setStatus(status);
        order.setAmount(new BigDecimal("1000.00"));
        order.setCreatedDate(LocalDateTime.now());
        return order;
    }
} 