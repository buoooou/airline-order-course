package com.postion.airlineorderbackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(@RequestParam Long userid) {
        try {
            List<OrderDTO> orderDtos = orderService.getAllOrders(userid);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取订单列表成功");
            result.put("data", orderDtos);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取订单列表失败");
            error.put("error", "GET_ORDERS_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            OrderDTO orderDto = orderService.getOrderByOrderNumber(orderNumber);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取订单详情成功");
            result.put("data", orderDto);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取订单详情失败");
            error.put("error", "GET_ORDER_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id) {
        try {
            OrderDTO orderDto = orderService.payOrder(id);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单状态更新成功");
            result.put("data", orderDto);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "订单状态更新失败");
            error.put("error", "UPDATE_STATUS_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            OrderDTO orderDto = orderService.cancelOrder(id);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单状态更新成功");
            result.put("data", orderDto);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "订单状态更新失败");
            error.put("error", "UPDATE_STATUS_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO requestOrder) {
        try {
            OrderDTO orderDto = orderService.createOrder(requestOrder);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("data", orderDto);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "订单创建失败");
            error.put("error", "UPDATE_STATUS_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
