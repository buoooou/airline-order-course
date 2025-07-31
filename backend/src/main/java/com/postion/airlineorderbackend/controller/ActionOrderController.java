package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/action")
@RequiredArgsConstructor
public class ActionOrderController {

    private final OrderService orderService;

    /**
     * 更新订单状态为已支付
     * 
     * @param orderId 订单ID
     * @return 操作结果
     */
    @PutMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<?>> payOrder(@PathVariable Long orderId) {
        try {
            orderService.payOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("订单支付成功"));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 操作结果
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("订单取消成功"));
        } catch (Exception e) {
            throw e;
        }
    }
}