package com.airline.order.controller;

import com.airline.order.dto.ApiResponse;
import com.airline.order.dto.CreateOrderRequest;
import com.airline.order.dto.OrderDTO;
import com.airline.order.enums.OrderStatus;
import com.airline.order.exception.BusinessException;
import com.airline.order.exception.GlobalExceptionHandler;
import com.airline.order.exception.ResourceNotFoundException;
import com.airline.order.exception.ValidationException;
import com.airline.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OrderController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 添加全局异常处理器
        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        // 准备测试数据
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setFlightId(1L);
        request.setSeatNumber("12A");
        request.setAmount(new BigDecimal("1500.00"));

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setOrderNumber("ORD20240101001");
        orderDTO.setUserId(1L);
        orderDTO.setUsername("testuser");
        orderDTO.setFlightId(1L);
        orderDTO.setFlightNumber("CA1234");
        orderDTO.setSeatNumber("12A");
        orderDTO.setAmount(new BigDecimal("1500.00"));
        orderDTO.setStatus(OrderStatus.PENDING_PAYMENT);
        orderDTO.setCreationDate(LocalDateTime.now());

        // Mock service 方法
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderDTO);

        // 执行测试
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单创建成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD20240101001"))
                .andExpect(jsonPath("$.data.flightNumber").value("CA1234"))
                .andExpect(jsonPath("$.data.seatNumber").value("12A"))
                .andExpect(jsonPath("$.data.amount").value(1500.00))
                .andExpect(jsonPath("$.data.status").value("PENDING_PAYMENT"));

        // 验证 service 方法被调用
        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void testCreateOrder_InvalidData() throws Exception {
        // 准备测试数据
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setFlightId(1L);
        request.setSeatNumber("12A");
        request.setAmount(new BigDecimal("1500.00"));

        // Mock service 抛出异常
        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenThrow(new ValidationException("座位已被占用"));

        // 执行测试
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("座位已被占用"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void testGetOrders_WithoutFilters() throws Exception {
        // 准备测试数据
        OrderDTO order1 = createTestOrderDTO(1L, "ORD001", OrderStatus.PAID);
        OrderDTO order2 = createTestOrderDTO(2L, "ORD002", OrderStatus.TICKETED);
        List<OrderDTO> orders = Arrays.asList(order1, order2);
        Page<OrderDTO> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 2);

        // Mock service 方法
        when(orderService.getOrders(null, null, 0, 10)).thenReturn(orderPage);

        // 执行测试
        mockMvc.perform(get("/api/orders")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10));

        verify(orderService, times(1)).getOrders(null, null, 0, 10);
    }

    @Test
    void testGetOrders_WithFilters() throws Exception {
        // 准备测试数据
        OrderDTO order1 = createTestOrderDTO(1L, "ORD001", OrderStatus.PAID);
        List<OrderDTO> orders = Arrays.asList(order1);
        Page<OrderDTO> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 1);

        // Mock service 方法
        when(orderService.getOrders(1L, "PAID", 0, 10)).thenReturn(orderPage);

        // 执行测试
        mockMvc.perform(get("/api/orders")
                .param("userId", "1")
                .param("status", "PAID")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders.length()").value(1))
                .andExpect(jsonPath("$.data.orders[0].status").value("PAID"));

        verify(orderService, times(1)).getOrders(1L, "PAID", 0, 10);
    }

    @Test
    void testCancelOrder_Success() throws Exception {
        // 准备测试数据
        Long orderId = 1L;
        OrderDTO orderDTO = createTestOrderDTO(orderId, "ORD001", OrderStatus.CANCELLED);

        // Mock service 方法
        when(orderService.cancelOrder(orderId)).thenReturn(orderDTO);

        // 执行测试
        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单已取消"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void testCancelOrder_OrderNotFound() throws Exception {
        // 准备测试数据
        Long orderId = 999L;

        // Mock service 抛出异常
        when(orderService.cancelOrder(orderId))
                .thenThrow(new ResourceNotFoundException("订单", orderId));

        // 执行测试
        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单不存在，ID: 999"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void testUpdateOrderStatus_Success() throws Exception {
        // 准备测试数据
        Long orderId = 1L;
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "PAID");

        OrderDTO orderDTO = createTestOrderDTO(orderId, "ORD001", OrderStatus.PAID);

        // Mock service 方法
        when(orderService.updateOrderStatus(orderId, "PAID")).thenReturn(orderDTO);

        // 执行测试
        mockMvc.perform(put("/api/orders/{orderId}/status", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单状态已更新"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("PAID"));

        verify(orderService, times(1)).updateOrderStatus(orderId, "PAID");
    }

    @Test
    void testUpdateOrderStatus_InvalidStatus() throws Exception {
        // 准备测试数据
        Long orderId = 1L;
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "INVALID_STATUS");

        // Mock service 抛出异常
        when(orderService.updateOrderStatus(orderId, "INVALID_STATUS"))
                .thenThrow(new BusinessException("无效的订单状态"));

        // 执行测试
        mockMvc.perform(put("/api/orders/{orderId}/status", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无效的订单状态"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(orderService, times(1)).updateOrderStatus(orderId, "INVALID_STATUS");
    }

    @Test
    void testGetOrderByNumber_Success() throws Exception {
        // 准备测试数据
        String orderNumber = "ORD20240101001";
        OrderDTO orderDTO = createTestOrderDTO(1L, orderNumber, OrderStatus.PAID);

        // Mock service 方法
        when(orderService.getOrderByNumber(orderNumber)).thenReturn(orderDTO);

        // 执行测试
        mockMvc.perform(get("/api/orders/by-number/{orderNumber}", orderNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.status").value("PAID"));

        verify(orderService, times(1)).getOrderByNumber(orderNumber);
    }

    @Test
    void testGetOrderByNumber_OrderNotFound() throws Exception {
        // 准备测试数据
        String orderNumber = "NONEXISTENT";

        // Mock service 抛出异常
        when(orderService.getOrderByNumber(orderNumber))
                .thenThrow(new ResourceNotFoundException("订单号为 " + orderNumber + " 的订单"));

        // 执行测试
        mockMvc.perform(get("/api/orders/by-number/{orderNumber}", orderNumber))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单号为 NONEXISTENT 的订单不存在"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(orderService, times(1)).getOrderByNumber(orderNumber);
    }

    /**
     * 创建测试用的OrderDTO对象
     */
    private OrderDTO createTestOrderDTO(Long id, String orderNumber, OrderStatus status) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(id);
        orderDTO.setOrderNumber(orderNumber);
        orderDTO.setUserId(1L);
        orderDTO.setUsername("testuser");
        orderDTO.setFlightId(1L);
        orderDTO.setFlightNumber("CA1234");
        orderDTO.setSeatNumber("12A");
        orderDTO.setAmount(new BigDecimal("1500.00"));
        orderDTO.setStatus(status);
        orderDTO.setCreationDate(LocalDateTime.now());
        return orderDTO;
    }
}