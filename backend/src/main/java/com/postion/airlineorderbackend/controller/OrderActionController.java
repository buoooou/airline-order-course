package com.postion.airlineorderbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.dto.request.PostOrderActionRequestDto;
import com.postion.airlineorderbackend.dto.response.CommonResponseDto;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.exception.InvalidOrderStatusException;
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
@RequestMapping("/api/orderact")
public class OrderActionController {

  private final OrderService orderService;

  private final AuthUtil authUtil;

  /**
   * Pay order.
   * 
   * @param auth       Authentication.
   * @param requestDto Request dto.
   * @return response.
   */
  @PostMapping("/pay")
  @Operation(summary = "支付", description = "对订单进行支付操作")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "成功完成支付操作", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
      @ApiResponse(responseCode = "401", description = "未授权")
  })
  public ResponseEntity<CommonResponseDto<OrderDto>> postPay(Authentication auth,
      @RequestBody PostOrderActionRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrderId() == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid request.", null).badRequest();
    }

    UserDto userDto = null;
    try {
      userDto = authUtil.getUserDetails(auth);
    } catch (UserNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, e.getMessage(), null).ok();
    }

    try {
      OrderDto result = orderService.payOrder(requestDto.getOrderId(), userDto.getUserid());
      return new CommonResponseDto<OrderDto>(true, 200, "", result).ok();
    } catch (DataNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order not found.", null).ok();
    } catch (UserNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid user.", null).ok();
    } catch (InvalidOrderStatusException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order is not in correct status.", null).ok();
    }

  }

  /**
   * Cancel order.
   * 
   * @param auth       Authentication.
   * @param requestDto Request dto.
   * @return response.
   */
  @PostMapping("/cancel")
  @Operation(summary = "取消", description = "对订单进行取消操作")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "成功完成取消操作", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
      @ApiResponse(responseCode = "401", description = "未授权")
  })
  public ResponseEntity<CommonResponseDto<OrderDto>> postCancel(Authentication auth,
      @RequestBody PostOrderActionRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrderId() == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid request.", null).badRequest();
    }

    UserDto userDto = null;
    try {
      userDto = authUtil.getUserDetails(auth);
    } catch (UserNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, e.getMessage(), null).ok();
    }

    try {
      OrderDto result = orderService.cancelOrder(requestDto.getOrderId(), userDto.getUserid());
      return new CommonResponseDto<OrderDto>(true, 200, "", result).ok();
    } catch (DataNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order not found.", null).ok();
    } catch (UserNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid user.", null).ok();
    } catch (InvalidOrderStatusException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order is not in correct status.", null).ok();
    }

  }

  /**
   * Retry order, requested by user.
   * 
   * @param auth       Authentication.
   * @param requestDto Request dto.
   * @return response.
   */
  @PostMapping("/retry")
  @Operation(summary = "重试出票", description = "对订单进行重试出票操作")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "成功完成重试出票操作", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
      @ApiResponse(responseCode = "401", description = "未授权")
  })
  public ResponseEntity<CommonResponseDto<OrderDto>> postRetryTicketing(Authentication auth,
      @RequestBody PostOrderActionRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrderId() == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid request.", null).badRequest();
    }

    UserDto userDto = null;
    try {
      userDto = authUtil.getUserDetails(auth);
    } catch (UserNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, e.getMessage(), null).ok();
    }

    try {
      OrderDto result = orderService.retryOrder(requestDto.getOrderId(), userDto.getUserid());
      return new CommonResponseDto<OrderDto>(true, 200, "", result).ok();
    } catch (DataNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order not found.", null).ok();
    } catch (UserNotFoundException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid user.", null).ok();
    } catch (InvalidOrderStatusException e) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order is not in correct status.", null).ok();
    }

  }

}
