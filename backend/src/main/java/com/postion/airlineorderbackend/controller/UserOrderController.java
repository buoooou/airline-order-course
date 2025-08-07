package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.CreateOrderRequest;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.entity.AppUser;
import com.postion.airlineorderbackend.repository.AppUserRepository;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;
    private final AppUserRepository appUserRepository;

    /**
     * 获取当前用户的所有订单
     * 
     * @return 当前用户的订单列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            AppUser user = appUserRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            
            List<OrderDto> orders = orderService.getOrdersByUserId(user.getId());
            
            return ResponseEntity.ok(ApiResponse.success("获取订单列表成功", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取订单列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取当前用户的单个订单详情
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getMyOrderById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            AppUser user = appUserRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

            OrderDto order = orderService.getOrderById(id);

            if (!order.getUserId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("您无权查看此订单"));
            }

            return ResponseEntity.ok(ApiResponse.success("成功获取订单详情", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取订单失败: " + e.getMessage()));
        }
    }

    /**
     * 创建新订单
     * 
     * @param createRequest 创建订单请求
     * @return 创建的订单信息
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@RequestBody CreateOrderRequest createRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            AppUser user = appUserRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

            OrderDto orderDto = orderService.createOrder(
                    user.getId(),
                    createRequest.flightId(),
                    createRequest.amount());

            return ResponseEntity.ok(ApiResponse.success("订单创建成功", orderDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("创建订单失败: " + e.getMessage()));
        }
    }

}