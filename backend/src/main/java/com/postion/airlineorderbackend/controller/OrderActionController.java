package com.postion.airlineorderbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderActionController {

    private final OrderService orderService;

    public OrderActionController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderDto> pay(@PathVariable Long id) {
        try {
            OrderDto order = orderService.payOrder(id);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Long id) {
        try {
            OrderDto order = orderService.cancelOrder(id);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/retry-ticketing")
    public ResponseEntity<Void> retryTicketing(@PathVariable Long id) {
        orderService.requestTicketIssuance(id);
        return ResponseEntity.accepted().build();
    }
}
