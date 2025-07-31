package airlaneorderbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    
    @Mock //创建一个 OrderRepository 的模拟对象
    private OrderRepository orderRepository;
    
    @InjectMocks //创建 OrderServiceImpl 实例，并自动注入上面 @Mock 标记的模拟对象
    private OrderService orderService;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        //在每个测试运行前，准备好测试数据
        testUser=new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
       // testUser.setPassword("zaq12wsx");

        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setOrderNumber("12345678");
        testOrder.setStatus(OrderStatus.PaiD);
        testOrder.setAmount(new BigDecimal("100.00"));
        testOrder.setCreateionDate(LocalDateTime.now());
        testOrder.setUser(testUser);
    }

    @Test
    @DisplayName("当调用 getAllOrders 时，应返回所有订单的 DTO 列表")
    void shouldReturnAllOrdersAsDtoList() {

        List<Order> orders = orderRepository.findAll();

        when(orders).thenReturn(Collections.singletonList(testOrder));

        List<OrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123456", result.get(0).getOrderNumber());
        assertEquals("testuser", result.get(0).getUser());

        verify(orderRepository, times(1)).findAll();

    }

}
