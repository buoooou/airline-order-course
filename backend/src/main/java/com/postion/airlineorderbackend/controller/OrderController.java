package com.postion.airlineorderbackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.dto.request.GetOrderByIdRequestDto;
import com.postion.airlineorderbackend.dto.response.CommonResponseDto;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.exception.UserNotFoundException;
import com.postion.airlineorderbackend.service.OrderService;
import com.postion.airlineorderbackend.util.AuthUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

  private final OrderService orderService;

  private final AuthUtil authUtil;

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
  public ResponseEntity<CommonResponseDto<OrderDto>> postGetOrderById(@RequestBody GetOrderByIdRequestDto requestDto) {
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
