package com.position.airline_order_course.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.position.airline_order_course.dto.OrderDto;
import com.position.airline_order_course.model.Order;
import com.position.airline_order_course.repo.OrderRepository;
import com.position.airline_order_course.service.Impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order1;
    private Order order2;
    private OrderDto orderDto1;
    private OrderDto orderDto2;

    @BeforeEach
    void setup() {

        order1 = new Order();
        order1.setId(1L);
        order1.setOrderNumber("Order001");
        order1.setAmount("11111.11");

        order2 = new Order();
        order2.setId(2L);
        order2.setOrderNumber("Order002");
        order2.setAmount("22222.22");

        orderDto1 = new OrderDto();
        orderDto1.setId(1L);
        orderDto1.setOrderNumber("Order001");
        orderDto1.setAmount("11111.11");

        orderDto2 = new OrderDto();
        orderDto2.setId(2L);
        orderDto2.setOrderNumber("Order002");
        orderDto2.setAmount("22222.22");

    }

    @Test
    void getAllOrders_shouldReturnList() {

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        // 执行被测试的方法
        List<OrderDto> result = orderService.getAllOrders();

        // 验证结果
        assertEquals(2, result.size());
        // 检查list第一个元素是否与orderDto1 相等
        assertEquals(orderDto1, result.get(0));
        // 检查list第二个元素是否与orderDto2 相等
        assertEquals(orderDto2, result.get(1));
        // 验证findAll()是否被调用了一次
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_shouldReturnOrderDto() {

        Long orderId = 1L;
        when(orderRepository.getOrderById(orderId)).thenReturn(order1);

        // 执行被测试的方法
        OrderDto result = orderService.getOrderById(orderId);

        // 验证结果
        assertNotNull(result);
        assertEquals(orderDto1, result);
        // 验证getOrderById(1L)是否被调用了一次
        verify(orderRepository, times(1)).getOrderById(orderId);
    }

}
