package com.postion.airlineorderbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.postion.airlineorderbackend.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private OrderServiceImpl orderService;

  private User testUser;

  private Order testOrder;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(10L);
    testUser.setUsername("test001");

    testOrder = new Order();
    testOrder.setId(1L);
    testOrder.setOrderNumber("TEST001");
    testOrder.setAmount(new BigDecimal("1234.56"));
    testOrder.setCreationDate(LocalDateTime.now());
    testOrder.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
    testOrder.setUser(testUser);
  }

  @Test
  @DisplayName("(mock)test orderService.getAllOrders(), should get the mock order")
  void shouldGetMockOrder() {
    when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

    List<OrderDto> allOrders = orderService.getAllOrders();
    assertNotNull(allOrders);
    assertEquals(1, allOrders.size());
  }

}
