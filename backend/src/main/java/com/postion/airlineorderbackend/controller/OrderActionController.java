package com.postion.airlineorderbackend.controller;

import java.util.ConcurrentModificationException;

import javax.naming.ServiceUnavailableException;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders/{id}")
@RequiredArgsConstructor
public class OrderActionController {
    private final OrderService orderService;

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<OrderDto>> pay(@PathVariable Long id){
        try{
            return ResponseEntity.ok(ApiResponse.success(orderService.payOrder(id)));
        } catch(IllegalStateException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancel(@PathVariable Long id){
        try{
            return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(id)));
        } catch(IllegalStateException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/retry-ticketing")
    @Operation(summary = "重新尝试出票", description = "提交异步任务以重新尝试为订单出票")
    public ResponseEntity<ApiResponse<?>> retryTicketing(@PathVariable Long id){
        try {
            orderService.requestTicketIssuance(id);
            return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id)));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
