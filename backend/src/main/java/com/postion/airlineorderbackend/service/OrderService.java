package com.postion.airlineorderbackend.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.postion.airlineorderbackend.dto.OrderDto;

public interface OrderService {
    OrderDto getOrderById(Long id);

    List<OrderDto> getAllOrders();

    Page<OrderDto> getAllOrders(Pageable pageable);

    OrderDto payOrder(Long id);

    OrderDto cancelOrder(Long id);

    void cancelUnpaidOrders();

    void requestTicketIssuance(Long id);

    OrderDto createOrder(OrderDto orderDto);

    OrderDto updateOrder(Long id, OrderDto orderDto);

    void deleteOrder(Long id);
}
