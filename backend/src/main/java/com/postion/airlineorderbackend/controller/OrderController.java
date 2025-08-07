package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

//    @GetMapping("/{id}")
//    public OrderDto getOrderById(@PathVariable Long id) {
//        return orderService.getOrderById(id);
//    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取订单详情", description = "根据订单ID获取订单的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取订单详情",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "订单不存在"),
            @ApiResponse(responseCode = "401", description = "未授权")
    })
    public ResponseEntity<com.postion.airlineorderbackend.dto.ApiResponse<OrderDto>> getOrderById(@Parameter(description = "订单ID") @PathVariable Long id) {
        return ResponseEntity.ok(com.postion.airlineorderbackend.dto.ApiResponse.success(orderService.getOrderById(id)));
    }
}