package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.statemachine.OrderEvent;
import com.postion.airlineorderbackend.statemachine.OrderStateContext;
import com.postion.airlineorderbackend.service.OrderService;
import com.postion.airlineorderbackend.statemachine.OrderStateMachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/action")
@RequiredArgsConstructor
public class OrderActionController {

    private final OrderService orderService;
    private final OrderStateMachineService stateMachineService;

    /**
     * 更新订单状态为已支付
     * 
     * @param orderId 订单ID
     * @return 操作结果
     */
    @PutMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<?>> payOrder(@PathVariable Long orderId) {
        try {
            // 获取订单实体
            Order order = orderService.findOrderById(orderId);
            
            // 创建状态上下文
            OrderStateContext context = new OrderStateContext(order);
            context.setOperator("user");
            context.setRemark("用户支付订单");

            // 使用状态机触发支付事件
            boolean success = stateMachineService.triggerStateTransition(
                orderId, OrderEvent.PAY, context, order.getUserId(), "user");

            if (!success) {
                return ResponseEntity.badRequest().body(ApiResponse.error("订单状态不允许支付"));
            }

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
            // 获取订单实体
            Order order = orderService.findOrderById(orderId);
            
            // 创建状态上下文
            OrderStateContext context = new OrderStateContext(order);
            context.setOperator("user");
            context.setRemark("用户取消订单");

            // 使用状态机触发取消事件
            boolean success = stateMachineService.triggerStateTransition(
                orderId, OrderEvent.CANCEL, context, order.getUserId(), "user");

            if (!success) {
                return ResponseEntity.badRequest().body(ApiResponse.error("订单状态不允许取消"));
            }

            return ResponseEntity.ok(ApiResponse.success("订单取消成功"));
        } catch (Exception e) {
            throw e;
        }
    }
}