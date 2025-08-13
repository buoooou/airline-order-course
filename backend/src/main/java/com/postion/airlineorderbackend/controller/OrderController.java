package com.postion.airlineorderbackend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
private final OrderService orderService;

    @GetMapping
    @Operation(summary = "获取所有订单", description = "返回系统中所有的订单列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功获取订单列表"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Map<String, Object> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());
        Page<OrderDto> orderPage = orderService.getAllOrders(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderPage.getContent());
        response.put("total", orderPage.getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取订单", description = "根据订单ID查询订单详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功获取订单详情"),
            @ApiResponse(responseCode = "404", description = "订单不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public OrderDto getOrderById(@PathVariable Long id){
        return orderService.getOrderById(id);
    }

    @PostMapping
    @Operation(summary = "创建订单", description = "创建一个新的订单")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功创建订单"),
            @ApiResponse(responseCode = "400", description = "请求参数无效"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public OrderDto createOrder(@RequestBody OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新订单", description = "根据订单ID更新订单信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功更新订单"),
            @ApiResponse(responseCode = "404", description = "订单不存在"),
            @ApiResponse(responseCode = "400", description = "请求参数无效"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public OrderDto updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return orderService.updateOrder(id, orderDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单", description = "根据订单ID删除订单")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功删除订单"),
            @ApiResponse(responseCode = "404", description = "订单不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
