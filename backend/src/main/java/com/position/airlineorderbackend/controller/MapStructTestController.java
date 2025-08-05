package com.position.airlineorderbackend.controller;

import com.position.airlineorderbackend.mapper.OrderMapper;
import com.position.airlineorderbackend.mapper.UserMapper;
import com.position.airlineorderbackend.mapper.FlightInfoMapper;
import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.model.User;
import com.position.airlineorderbackend.model.FlightInfo;
import com.position.airlineorderbackend.dto.OrderDto;
import com.position.airlineorderbackend.dto.RegisterRequest;
import com.position.airlineorderbackend.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MapStruct测试控制器
 * 演示MapStruct的各种映射功能
 */
@RestController
@RequestMapping("/mapstruct-test")
public class MapStructTestController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FlightInfoMapper flightInfoMapper;

    /**
     * 测试Order实体到DTO的映射
     */
    @GetMapping("/order-to-dto")
    public OrderDto testOrderToDto() {
        // 创建测试Order实体
        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-2024-001");
        order.setStatus(com.position.airlineorderbackend.model.OrderStatus.PENDING_PAYMENT);
        order.setAmount(new BigDecimal("1500.00"));
        order.setCreatedDate(LocalDateTime.now());

        // 使用MapStruct进行映射
        OrderDto orderDto = orderMapper.toDto(order);
        
        return orderDto;
    }

    /**
     * 测试OrderDto到实体的映射
     */
    @GetMapping("/dto-to-order")
    public String testDtoToOrder() {
        // 创建测试OrderDto
        OrderDto orderDto = new OrderDto();
        orderDto.setId(2L);
        orderDto.setOrderNumber("ORD-2024-002");
        orderDto.setStatus(com.position.airlineorderbackend.model.OrderStatus.PAID);
        orderDto.setAmount(new BigDecimal("2000.00"));
        orderDto.setCreatedDate(LocalDateTime.now());
        orderDto.setUserId(1L);
        orderDto.setFlightInfoId(1L);

        // 使用MapStruct进行映射
        Order order = orderMapper.toEntity(orderDto);
        
        return "DTO转换为实体成功: " + order.getOrderNumber();
    }

    /**
     * 测试Order列表映射
     */
    @GetMapping("/order-list")
    public List<OrderDto> testOrderList() {
        // 创建测试Order列表
        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderNumber("ORD-2024-001");
        order1.setStatus(com.position.airlineorderbackend.model.OrderStatus.PENDING_PAYMENT);
        order1.setAmount(new BigDecimal("1500.00"));
        order1.setCreatedDate(LocalDateTime.now());

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderNumber("ORD-2024-002");
        order2.setStatus(com.position.airlineorderbackend.model.OrderStatus.PAID);
        order2.setAmount(new BigDecimal("2000.00"));
        order2.setCreatedDate(LocalDateTime.now());

        List<Order> orders = List.of(order1, order2);

        // 使用MapStruct进行列表映射
        return orderMapper.toDtoList(orders);
    }

    /**
     * 测试User映射
     */
    @GetMapping("/user-mapping")
    public AuthResponse testUserMapping() {
        // 创建测试User实体
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");

        // 使用MapStruct进行映射
        AuthResponse authResponse = userMapper.toAuthResponse(user);
        
        return authResponse;
    }

    /**
     * 测试RegisterRequest到User的映射
     */
    @GetMapping("/register-to-user")
    public String testRegisterToUser() {
        // 创建测试RegisterRequest
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole("USER");

        // 使用MapStruct进行映射
        User user = userMapper.toEntity(registerRequest);
        
        return "RegisterRequest转换为User成功: " + user.getUsername();
    }

    /**
     * 测试FlightInfo映射
     */
    @GetMapping("/flight-info")
    public FlightInfoMapper.FlightInfoDto testFlightInfoMapping() {
        // 创建测试FlightInfo实体
        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setId(1L);
        flightInfo.setFlightNumber("CA1234");
        flightInfo.setDeparture("北京");
        flightInfo.setDestination("上海");
        flightInfo.setDepartureTime("2024-01-15 10:00:00");

        // 使用MapStruct进行映射
        return flightInfoMapper.toDto(flightInfo);
    }

    /**
     * 测试实体更新
     */
    @GetMapping("/update-entity")
    public String testUpdateEntity() {
        // 创建原始Order实体
        Order originalOrder = new Order();
        originalOrder.setId(1L);
        originalOrder.setOrderNumber("ORD-2024-001");
        originalOrder.setStatus(com.position.airlineorderbackend.model.OrderStatus.PENDING_PAYMENT);
        originalOrder.setAmount(new BigDecimal("1500.00"));
        originalOrder.setCreatedDate(LocalDateTime.now());

        // 创建更新的OrderDto
        OrderDto updateDto = new OrderDto();
        updateDto.setAmount(new BigDecimal("1800.00"));
        updateDto.setStatus(com.position.airlineorderbackend.model.OrderStatus.PAID);

        // 使用MapStruct更新实体
        orderMapper.updateEntity(originalOrder, updateDto);
        
        return "实体更新成功: 金额=" + originalOrder.getAmount() + ", 状态=" + originalOrder.getStatus();
    }
} 