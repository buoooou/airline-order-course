package com.postion.airlineorderbackend.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/orders/{id}")
@RequiredArgsConstructor
public class OrderActionController {

    private final OrderService orderService;

    @PostMapping("/pay")
    public ResponseEntity<OrderDto> pay(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.payOrder(id));
        } catch (Exception e) {    
            return ResponseEntity.badRequest().body(null);     
        }
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.cancelOrder(id));
        } catch (Exception e) {    
            return ResponseEntity.badRequest().body(null);     
        }
    }
    
    @PostMapping("/retry-ticketing")
    public ResponseEntity<OrderDto> retryTicketing(@PathVariable Long id) {
        orderService.retryTicketingIssuance(id);
        return ResponseEntity.accepted().build();
    }

}
