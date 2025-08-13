package com.postion.airlineorderbackend.controller;

import net.javacrumbs.shedlock.support.LockException;

import java.util.ConcurrentModificationException;

import org.springframework.http.HttpStatus;
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

    /**
     * 处理订单支付请求。
     *
     * @param id 订单的唯一标识符。
     * @return 包含订单支付结果的响应实体。如果支付成功，返回订单信息；如果支付失败（例如订单状态非法），返回错误响应。
     * @throws IllegalStateException 如果订单状态不满足支付条件。
     */
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<OrderDto>> pay(@PathVariable Long id){
        try{
            return ResponseEntity.ok(ApiResponse.success(orderService.payOrder(id)));
        } catch(IllegalStateException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancel(@PathVariable Long id) {
        try {
            OrderDto orderDto = orderService.cancelOrder(id);
            if (orderDto == null) {
                return ResponseEntity.internalServerError().body(null);
            } else {
                return ResponseEntity.ok(ApiResponse.success(orderDto));
            }
        } catch (IllegalStateException e) {
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
