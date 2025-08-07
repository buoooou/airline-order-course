package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders/{id}")
@RequiredArgsConstructor
public class OrderActionController {
	private final OrderService orderService;

	@PostMapping("/pay")
	@Operation(summary = "支付订单请求", description = "根据订单ID支付订单")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "成功支付订单",
	                content = @Content(mediaType = "application/json",
	                        schema = @Schema(implementation = OrderDto.class))),
	        @ApiResponse(responseCode = "401", description = "未授权"),
	        @ApiResponse(responseCode = "404", description = "用户不存在")
	})
	public ResponseEntity<OrderDto> pay(@Parameter(description = "订单ID") @PathVariable Long id) {
		try {
			return ResponseEntity.ok(orderService.payOrder(id));
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping("/cancel")
	@Operation(summary = "取消订单请求", description = "根据订单ID取消订单")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "成功取消订单",
	                content = @Content(mediaType = "application/json",
	                        schema = @Schema(implementation = OrderDto.class))),
	        @ApiResponse(responseCode = "401", description = "未授权"),
	        @ApiResponse(responseCode = "404", description = "用户不存在")
	})
	public ResponseEntity<OrderDto> cancel(@Parameter(description = "订单ID") @PathVariable Long id) {
		try {
			return ResponseEntity.ok(orderService.cancelOrder(id));
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping("/retry-ticketing")
	@Operation(summary = "订单出票重试请求", description = "根据订单ID重试订单出票")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "订单出票成功",
	                content = @Content(mediaType = "application/json",
	                        schema = @Schema(implementation = OrderDto.class))),
	        @ApiResponse(responseCode = "401", description = "未授权"),
	        @ApiResponse(responseCode = "404", description = "用户不存在")
	})
	public ResponseEntity<Void> retryTicketing(@Parameter(description = "订单ID") @PathVariable Long id) {
		orderService.requestTicketIssuance(id);
		return ResponseEntity.accepted().build();
	}
}