package com.airline.order.service;

import com.airline.order.dto.CreateOrderRequest;
import com.airline.order.dto.OrderDTO;
import com.airline.order.entity.Order;
import com.airline.order.entity.User;
import com.airline.order.entity.FlightInfo;
import com.airline.order.enums.OrderStatus;
import com.airline.order.repository.OrderRepository;
import com.airline.order.repository.UserRepository;
import com.airline.order.repository.FlightInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OrderService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FlightInfoRepository flightInfoRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private FlightInfo testFlight;
    private Order testOrder;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");

        // 创建测试航班
        testFlight = new FlightInfo();
        testFlight.setId(1L);
        testFlight.setFlightNumber("CA1234");
        testFlight.setDepartureAirportCode("PEK");
        testFlight.setDepartureAirportName("北京首都国际机场");
        testFlight.setArrivalAirportCode("SHA");
        testFlight.setArrivalAirportName("上海虹桥国际机场");
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        testFlight.setFlightDuration(120);

        // 创建测试订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD001");
        testOrder.setUser(testUser);
        testOrder.setFlightInfo(testFlight);
        testOrder.setSeatNumber("12A");
        testOrder.setAmount(new BigDecimal("1500.00"));
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrder.setCreationDate(LocalDateTime.now());

        // 创建订单请求
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId(1L);
        createOrderRequest.setFlightId(1L);
        createOrderRequest.setSeatNumber("12A");
        createOrderRequest.setAmount(new BigDecimal("1500.00"));
    }

    @Test
    void testCreateOrder_Success() {
        // 模拟依赖
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightInfoRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(orderRepository.findByFlightIdAndSeatNumber(1L, "12A")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        OrderDTO result = orderService.createOrder(createOrderRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("ORD001", result.getOrderNumber());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getStatus());
        assertEquals(new BigDecimal("1500.00"), result.getAmount());
        assertEquals("12A", result.getSeatNumber());

        // 验证方法调用
        verify(userRepository).findById(1L);
        verify(flightInfoRepository).findById(1L);
        verify(orderRepository).findByFlightIdAndSeatNumber(1L, "12A");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrder_UserNotFound() {
        // 模拟用户不存在
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(createOrderRequest);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_SeatAlreadyTaken() {
        // 模拟座位已被占用
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightInfoRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(orderRepository.findByFlightIdAndSeatNumber(1L, "12A")).thenReturn(Optional.of(testOrder));

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(createOrderRequest);
        });

        assertEquals("座位已被预订", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrders_NoFilter() {
        // 创建测试数据
        List<Order> orders = Arrays.asList(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("creationDate").descending());

        // 模拟依赖
        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        // 执行测试
        Page<OrderDTO> result = orderService.getOrders(null, null, 0, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("ORD001", result.getContent().get(0).getOrderNumber());

        verify(orderRepository).findAll(pageable);
    }

    @Test
    void testGetOrders_WithUserIdFilter() {
        // 创建测试数据
        List<Order> orders = Arrays.asList(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("creationDate").descending());

        // 模拟依赖
        when(orderRepository.findByUserId(1L, pageable)).thenReturn(orderPage);

        // 执行测试
        Page<OrderDTO> result = orderService.getOrders(1L, null, 0, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("ORD001", result.getContent().get(0).getOrderNumber());

        verify(orderRepository).findByUserId(1L, pageable);
    }

    @Test
    void testGetOrders_WithStatusFilter() {
        // 创建测试数据
        List<Order> orders = Arrays.asList(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("creationDate").descending());

        // 模拟依赖
        when(orderRepository.findByStatus(OrderStatus.PENDING_PAYMENT, pageable)).thenReturn(orderPage);

        // 执行测试
        Page<OrderDTO> result = orderService.getOrders(null, "PENDING_PAYMENT", 0, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getContent().get(0).getStatus());

        verify(orderRepository).findByStatus(OrderStatus.PENDING_PAYMENT, pageable);
    }

    @Test
    void testGetOrders_WithBothFilters() {
        // 创建测试数据
        List<Order> orders = Arrays.asList(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("creationDate").descending());

        // 模拟依赖
        when(orderRepository.findByUserIdAndStatus(1L, OrderStatus.PENDING_PAYMENT, pageable))
            .thenReturn(orderPage);

        // 执行测试
        Page<OrderDTO> result = orderService.getOrders(1L, "PENDING_PAYMENT", 0, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("ORD001", result.getContent().get(0).getOrderNumber());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getContent().get(0).getStatus());

        verify(orderRepository).findByUserIdAndStatus(1L, OrderStatus.PENDING_PAYMENT, pageable);
    }

    @Test
    void testCancelOrder_Success() {
        // 模拟依赖
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        OrderDTO result = orderService.cancelOrder(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        // 模拟订单不存在
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(1L);
        });

        assertEquals("订单不存在", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_AlreadyCancelled() {
        // 设置订单为已取消状态
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(1L);
        });

        assertEquals("订单状态不允许取消", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // 模拟依赖
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 执行测试
        OrderDTO result = orderService.updateOrderStatus(1L, "PAID");

        // 验证结果
        assertNotNull(result);
        assertEquals(OrderStatus.PAID, testOrder.getStatus());

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void testUpdateOrderStatus_InvalidStatus() {
        // 模拟订单存在
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(1L, "INVALID_STATUS");
        });

        assertEquals("无效的订单状态", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrderByOrderNumber_Success() {
        // 模拟依赖
        when(orderRepository.findByOrderNumber("ORD001")).thenReturn(Optional.of(testOrder));

        // 执行测试
        OrderDTO result = orderService.getOrderByNumber("ORD001");

        // 验证结果
        assertNotNull(result);
        assertEquals("ORD001", result.getOrderNumber());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getStatus());

        verify(orderRepository).findByOrderNumber("ORD001");
    }

    @Test
    void testGetOrderByOrderNumber_OrderNotFound() {
        // 模拟订单不存在
        when(orderRepository.findByOrderNumber("NONEXISTENT")).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderByNumber("NONEXISTENT");
        });

        assertEquals("订单不存在", exception.getMessage());
        verify(orderRepository).findByOrderNumber("NONEXISTENT");
    }

    @Test
    void testConvertToDTO() {
        // 使用反射或者创建一个public方法来测试convertToDTO
        // 这里我们通过其他方法间接测试
        when(orderRepository.findByOrderNumber("ORD001")).thenReturn(Optional.of(testOrder));

        OrderDTO result = orderService.getOrderByNumber("ORD001");

        // 验证DTO转换
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals(testOrder.getOrderNumber(), result.getOrderNumber());
        assertEquals(testOrder.getStatus(), result.getStatus());
        assertEquals(testOrder.getAmount(), result.getAmount());
        assertEquals(testOrder.getSeatNumber(), result.getSeatNumber());
        assertEquals(testOrder.getCreationDate(), result.getCreationDate());
        assertEquals(testOrder.getUser().getId(), result.getUserId());
        assertEquals(testOrder.getFlightInfo().getId(), result.getFlightId());
    }
}