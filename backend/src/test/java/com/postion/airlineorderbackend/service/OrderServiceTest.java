package com.postion.airlineorderbackend.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.impl.OrderServiceImpl;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
public class OrderServiceTest {

  private final OrderServiceImpl orderService;

  @Test
  @DisplayName("test orderService.getAllOrders(), should get all orders")
  void shouldGetAllOrders() {
    List<OrderDto> allOrders = orderService.getAllOrders();
    assertNotNull(allOrders);
    assertTrue(allOrders.size() > 0);
  }

}
