package com.postion.airlineorderbackend.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/orders/{id}")
@RequiredArgsConstructor
public class OrderActionController {

    private final OrderService orderService;

    @PostMapping("/pay")
    @Operation(summary = "订单支付", description = "订单支付")
    public ResponseEntity<ApiResponse<?>> pay(@PathVariable Long id) {
        try {
        	OrderDto order = orderService.payOrder(id);
            return ResponseEntity.ok(ApiResponse.success("订单支付成功", order));
        } catch (Exception e) {    
            throw e;   
        }
    }
    
    @PostMapping("/cancel")
    @Operation(summary = "订单取消", description = "订单取消")
    public ResponseEntity<ApiResponse<?>> cancel(@PathVariable Long id) {
        try {
        	OrderDto order = orderService.cancelOrder(id);
            return ResponseEntity.ok(ApiResponse.success("订单取消成功", order));
        } catch (Exception e) {    
            throw e;
        }
    }
    
    @PostMapping("/retry-ticketing")
    @Operation(summary = "重新出票", description = "重新出票")
    public ResponseEntity<OrderDto> retryTicketing(@PathVariable Long id) {
        orderService.retryTicketingIssuance(id);
        return ResponseEntity.accepted().build();
    }

}
