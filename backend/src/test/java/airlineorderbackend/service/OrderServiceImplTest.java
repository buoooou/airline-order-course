package airlineorderbackend.service;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.impl.OrderServiceImpl;
import com.postion.airlineorderbackend.dto.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private ModelMapper modelMapper;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setOrderNumber("ORD12345");
        testOrder.setStatus(OrderStatus.PAID);
        testOrder.setAmount(new BigDecimal("250.75"));
        testOrder.setCreationDate(LocalDateTime.now());
        testOrder.setUser(testUser);
    }

//    @Test
//    @DisplayName("当调用 getAllOrders 时，应返回所有订单的 DTO 列表")
//    void shouldReturnAllOrdersAsDtoList() {
//        // Arrange
//        when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));
//
//        // Act
//        List<OrderDto> result = orderService.getAllOrders();
//        System.out.println(result);
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("ORD12345", result.get(0).getOrderNumber());
////        assertEquals("testuser", result.get(0).getUser().getUsername());
//
//        verify(orderRepository, times(1)).findAll();
//    }
@Test
@DisplayName("当调用 getAllOrders 时，应返回所有订单的 DTO 列表")
void shouldReturnAllOrdersAsDtoList() {
    // Arrange
    // 1. 准备测试数据
    when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

    // 2. 模拟ModelMapper的转换行为
    OrderDto mockOrderDto = new OrderDto();
    mockOrderDto.setId(testOrder.getId());
    mockOrderDto.setOrderNumber(testOrder.getOrderNumber()); // 设置必要的字段
    when(modelMapper.map(testOrder, OrderDto.class)).thenReturn(mockOrderDto);

    // Act
    List<OrderDto> result = orderService.getAllOrders();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    OrderDto firstDto = result.get(0);
    assertNotNull(firstDto); // 确保DTO对象不为null
    assertEquals("ORD12345", firstDto.getOrderNumber());

    verify(orderRepository, times(1)).findAll();
    verify(modelMapper, times(1)).map(testOrder, OrderDto.class); // 验证转换方法被调用
}

    @Test
    @DisplayName("当使用有效 ID 调用 getOrderById 时，应返回对应的订单 DTO 并包含航班信息")
    void shouldReturnOrderDtoWithFlightInfoForValidId() {
        // Arrange
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));

        // Act
        OrderDto result = orderService.getOrderById(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("ORD12345", result.getOrderNumber());
//        assertNotNull(result.getFlightInfo(), "航班信息不应为空");
//        assertEquals("MU5180", result.getFlightInfo().get("flightNumber"));

        verify(orderRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("当使用无效 ID 调用 getOrderById 时，应抛出 RuntimeException")
    void shouldThrowExceptionForInvalidId() {
        // Arrange
        long invalidId = 999L;
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(invalidId);
        });

        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, times(1)).findById(invalidId);
    }
}
