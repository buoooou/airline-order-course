package com.postion.airlineorderbackend.service.impl; 

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORDER123");
        testOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        testOrder.setAmount(new BigDecimal("100.00"));
        testOrder.setCreationDate(LocalDateTime.now());
        testOrder.setUser(testUser);
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderDto orderDto = orderService.getOrderById(1L);

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getId()).isEqualTo(1L);
        assertThat(orderDto.getOrderNumber()).isEqualTo("ORDER123");
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

        List<OrderDto> orders = orderService.getAllOrders();

        assertThat(orders).isNotNull();
        assertThat(orders.size()).isEqualTo(1);
        assertThat(orders.get(0).getOrderNumber()).isEqualTo("ORDER123");
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testCreateOrder() {
        OrderDto dto = new OrderDto();
        dto.setOrderNumber("ORDER456");
        dto.setAmount(new BigDecimal("200.00"));
        dto.setUser(new UserDto());
        dto.getUser().setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(2L);
            return o;
        });

        OrderDto saved = orderService.createOrder(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(2L);
        assertThat(saved.getOrderNumber()).isEqualTo("ORDER456");
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrder() {
        OrderDto dto = new OrderDto();
        dto.setOrderNumber("ORDER789");
        dto.setAmount(new BigDecimal("300.00"));
        dto.setStatus(OrderStatus.PAID);
        dto.setUser(new UserDto());
        dto.getUser().setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto updated = orderService.updateOrder(1L, dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getOrderNumber()).isEqualTo("ORDER789");
        assertThat(updated.getAmount()).isEqualByComparingTo("300.00");
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testPayOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto paid = orderService.payOrder(1L);

        assertThat(paid.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testPayOrder_Fail() {
        testOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> orderService.payOrder(1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testCancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto cancelled = orderService.cancelOrder(1L);

        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrder_AlreadyCancelled() {
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testRequestTicketIssuance() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.requestTicketIssuance(1L);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.TICKETING_IN_PROGRESS);
    }
}

