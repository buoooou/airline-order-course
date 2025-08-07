package com.postion.airlineorderbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单控制器集成测试类
 * 测试订单控制器的所有REST端点
 * 
 * @author Postion
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDto testOrder;
    private UserDto testUser;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new UserDto();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");

        // 初始化测试订单
        testOrder = new OrderDto();
        testOrder.setOrderNumber("TEST-ORDER-001");
        testOrder.setAmount(new BigDecimal("299.99"));
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrder.setCreationDate(LocalDateTime.now());
        testOrder.setUser(testUser);
        
        Map<String, Object> flightInfo = new HashMap<>();
        flightInfo.put("flightNumber", "CA1234");
        flightInfo.put("departure", "北京");
        flightInfo.put("arrival", "上海");
        flightInfo.put("departureTime", "2024-12-01 08:00");
        testOrder.setFlightInfo(flightInfo);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.orderNumber", is("TEST-ORDER-001")))
                .andExpect(jsonPath("$.status", is("PENDING_PAYMENT")))
                .andExpect(jsonPath("$.amount", is(299.99)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllOrders() throws Exception {
        // 先创建一个订单
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetOrderById() throws Exception {
        // 创建订单
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andReturn().getResponse().getContentAsString();

        OrderDto createdOrder = objectMapper.readValue(response, OrderDto.class);
        Long orderId = createdOrder.getId();

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.intValue())))
                .andExpect(jsonPath("$.orderNumber", is("TEST-ORDER-001")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testPayOrder() throws Exception {
        // 创建订单
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andReturn().getResponse().getContentAsString();

        OrderDto createdOrder = objectMapper.readValue(response, OrderDto.class);
        Long orderId = createdOrder.getId();

        // 支付订单
        mockMvc.perform(post("/api/orders/" + orderId + "/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PAID")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCancelOrder() throws Exception {
        // 创建订单
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andReturn().getResponse().getContentAsString();

        OrderDto createdOrder = objectMapper.readValue(response, OrderDto.class);
        Long orderId = createdOrder.getId();

        // 先支付订单
        mockMvc.perform(post("/api/orders/" + orderId + "/pay"));

        // 取消订单
        mockMvc.perform(post("/api/orders/" + orderId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRequestTicketIssuance() throws Exception {
        // 创建订单
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andReturn().getResponse().getContentAsString();

        OrderDto createdOrder = objectMapper.readValue(response, OrderDto.class);
        Long orderId = createdOrder.getId();

        // 支付订单
        mockMvc.perform(post("/api/orders/" + orderId + "/pay"));

        // 请求出票
        mockMvc.perform(post("/api/orders/" + orderId + "/retry-ticketing"))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrder() throws Exception {
        // 创建订单
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andReturn().getResponse().getContentAsString();

        OrderDto createdOrder = objectMapper.readValue(response, OrderDto.class);
        Long orderId = createdOrder.getId();

        // 更新订单
        createdOrder.setAmount(new BigDecimal("399.99"));
        mockMvc.perform(put("/api/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(399.99)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteOrder() throws Exception {
        // 创建订单
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andReturn().getResponse().getContentAsString();

        OrderDto createdOrder = objectMapper.readValue(response, OrderDto.class);
        Long orderId = createdOrder.getId();

        // 删除订单
        mockMvc.perform(delete("/api/orders/" + orderId))
                .andExpect(status().isOk());

        // 验证订单已被删除
        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testForbiddenAccess() throws Exception {
        mockMvc.perform(put("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isForbidden());
    }
}