package com.postion.airlineorderbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderResponseDTO;
import com.postion.airlineorderbackend.entity.OrderStatus;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestParam String email,
            @RequestParam Long flightId) {
        return ResponseEntity.ok(orderService.createOrder(email, flightId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus nextStatus) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, nextStatus));
    }
}

