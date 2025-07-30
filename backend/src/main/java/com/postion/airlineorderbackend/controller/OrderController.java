package com.postion.airlineorderbackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.Exception.AirlineBusinessException;

import com.postion.airlineorderbackend.constants.Constants;
import com.postion.airlineorderbackend.dto.ApiResponseDTO;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "获取所有订单")
    @GetMapping("/all")
    public ApiResponseDTO<List<OrderDTO>> getAllOrders(@RequestParam Long userid) {
        try {
            List<OrderDTO> orderDtos = orderService.getAllOrders(userid);

            return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.GET_ALL_ORDERS_SUCCESS, orderDtos);
        } catch (AirlineBusinessException e) {
            return ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.GET_ALL_ORDERS_FAIL);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据订单编号获取订单详情", description = "根据订单编号获取订单的详情信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取订单详情成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "500", description = "获取订单详情失败")
    })
    public ApiResponseDTO<OrderDTO> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            OrderDTO orderDto = orderService.getOrderByOrderNumber(orderNumber);

            return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.GET_ORDER_SUCCESS, orderDto);
        } catch (AirlineBusinessException e) {
            return ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.GET_ORDER_FAIL);
        }
    }

    @Operation(summary = "支付订单")
    @PutMapping("/{id}/pay")
    public ApiResponseDTO<OrderDTO> payOrder(@PathVariable Long id) {
        try {
            OrderDTO orderDto = orderService.payOrder(id);

            return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.PAY_ORDER_SUCCESS, orderDto);
        } catch (AirlineBusinessException e) {
            return ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.PAY_ORDER_FAIL);
        }
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public ApiResponseDTO<OrderDTO> cancelOrder(@PathVariable Long id) {
        try {
            OrderDTO orderDto = orderService.cancelOrder(id);

            return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.CANCEL_ORDER_SUCCESS, orderDto);
        } catch (AirlineBusinessException e) {
            return ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.CANCEL_ORDER_FAIL);
        }
    }

    @Operation(summary = "创建订单")
    @PostMapping
    public ApiResponseDTO<OrderDTO> createOrder(@RequestBody OrderDTO requestOrder) {
        try {
            OrderDTO orderDto = orderService.createOrder(requestOrder);

            return ApiResponseDTO.success(HttpStatus.CREATED.value(), Constants.CREATE_ORDER_SUCCESS, orderDto);
        } catch (AirlineBusinessException e) {
            return ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.CREATE_ORDER_FAIL);
        }
    }
}
