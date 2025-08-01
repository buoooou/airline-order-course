package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final OrderService orderService;

    // 查询所有订单
    @GetMapping("/orders")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 查询单个订单
    @GetMapping("/orders/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // 新增订单
    @PostMapping("/orders")
    public OrderDto createOrder(@RequestBody OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }

    // 修改订单
    @PutMapping("/orders/{id}")
    public OrderDto updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return orderService.updateOrder(id, orderDto);
    }

    // 删除订单
    @DeleteMapping("/orders/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    // 支付订单
    @PostMapping("/orders/{id}/pay")
    public ResponseEntity<OrderDto> pay(@PathVariable Long id) {
        try {
            OrderDto order = orderService.payOrder(id);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 取消订单
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Long id) {
        try {
            OrderDto order = orderService.cancelOrder(id);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 请求出票
    @PostMapping("/orders/{id}/retry-ticketing")
    public ResponseEntity<Void> retryTicketing(@PathVariable Long id) {
        orderService.requestTicketIssuance(id);
        return ResponseEntity.accepted().build();
    }
}
