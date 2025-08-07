package com.postion.airlineorderbackend.controller;

import java.util.List;

import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.auth.AuthUtil;
import com.postion.airlineorderbackend.dto.CommonResponseDto;
import com.postion.airlineorderbackend.dto.OrderResponseDTO;
import com.postion.airlineorderbackend.dto.RequestDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.entity.OrderStatus;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.exception.UserNotFoundException;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/orders")
//@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService = null;
    private final AuthUtil authUtil = null;
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(
            @RequestParam String email,
            @RequestParam Long flightId) {
        return ResponseEntity.ok(orderService.createOrder(email, flightId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus nextStatus) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, nextStatus));
    }
    





    /**
     * Get all orders of current user.
     * 
     * @param auth Authentication.
     * @return response.
     */
    @PostMapping("/getOfUser")
    @Operation(summary = "获取用户订单", description = "获取当前登陆用户的所有订单列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取所有订单列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    public ResponseEntity<CommonResponseDto<List<OrderDto>>> postGetOrdersOfUser(Authentication auth) {
      UserDto userDto = null;
      try {
        userDto = authUtil.getUserDetails(auth);
      } catch (UserNotFoundException e) {
        return new CommonResponseDto<List<OrderDto>>(false, 200, e.getMessage(), null).ok();
      }

      List<OrderDto> orders = orderService.getAllOrdersByUserId(userDto.getUserid());
      CommonResponseDto<List<OrderDto>> res = new CommonResponseDto<List<OrderDto>>(true, 200, "", orders);
      return res.ok();
    }

    /**
     * Get order by order id.
     * 
     * @param requestDto Request dto.
     * @return response.
     */
    @PostMapping("/getById")
    @Operation(summary = "根据订单号获取订单", description = "根据订单号获取订单")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取订单", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    public ResponseEntity<CommonResponseDto<OrderDto>> postGetOrderById(@RequestBody RequestDto requestDto) {
      if (requestDto == null || requestDto.getOrderId() == null) {
        return new CommonResponseDto<OrderDto>(false, 200, "Invalid request.", null).badRequest();
      }

      CommonResponseDto<OrderDto> res = new CommonResponseDto<OrderDto>();
      try {
        OrderDto order = orderService.getOrderById(requestDto.getOrderId());
        res.setSuccess(true);
        res.setMessage("");
        res.setCode(200);
        res.setData(order);
      } catch (DataNotFoundException e) {
        res.setSuccess(false);
        res.setMessage("Order not found.");
        res.setCode(200);
        res.setData(null);
      }
      return res.ok();
    }

}

