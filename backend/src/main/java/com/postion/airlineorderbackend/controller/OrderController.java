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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

        private final OrderService orderService;

        /**
         * getAllOrders
         *
         */
        @GetMapping
        @Operation(summary = "Get the current user's orders", description = "Get all orders of the currently logged in user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully obtained user order list",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
                        @ApiResponse(responseCode = "401", description = "unauthorized"),
                        @ApiResponse(responseCode = "404", description = "user does not exist")
        })
        public ResponseEntity<com.postion.airlineorderbackend.dto.ApiResponse<List<OrderDto>>> getAllOrders() {
                return ResponseEntity.ok(
                                com.postion.airlineorderbackend.dto.ApiResponse.success(orderService.getAllOrders()));
        }

        /**
         * getOrderById
         *
         * @param id
         *
         */
        @GetMapping("/{id}")
        @Operation(summary = "Get order details", description = "Retrieve detailed information of a specific order By orderID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully obtained order details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
                        @ApiResponse(responseCode = "401", description = "unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Order does not exist")
        })
        public ResponseEntity<com.postion.airlineorderbackend.dto.ApiResponse<OrderDto>> getOrderById(
                        @Parameter(description = "ordeID", required = true) @PathVariable Long id) {
                return ResponseEntity.ok(
                                com.postion.airlineorderbackend.dto.ApiResponse.success(orderService.getOrderById(id)));
        }
}
