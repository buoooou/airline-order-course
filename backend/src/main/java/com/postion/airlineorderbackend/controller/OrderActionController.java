package com.postion.airlineorderbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.request.PostOrderActionRequestDto;
import com.postion.airlineorderbackend.dto.response.CommonResponseDto;
import com.postion.airlineorderbackend.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/orderact")
public class OrderActionController {

  @Autowired
  private OrderService orderService;

  /**
   * Pay order.
   * 
   * @param auth       Authentication.
   * @param requestDto Request dto.
   * @return response.
   */
  @PostMapping("/pay")
  public ResponseEntity<CommonResponseDto<OrderDto>> postPay(Authentication auth,
      @RequestBody PostOrderActionRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrderId() == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid request.", null).badRequest();
    }

    // get user id from authentication
    // in day 1 practice, use 1L instead
    // Long userid = Long.valueOf(auth.getUsername());
    Long userid = 1L;
    OrderDto result = orderService.payOrder(requestDto.getOrderId(), userid);
    if (result == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order not found or not in correct status.", null).ok();
    }
    return new CommonResponseDto<OrderDto>(true, 200, "", result).ok();
  }

  /**
   * Cancel order.
   * 
   * @param auth       Authentication.
   * @param requestDto Request dto.
   * @return response.
   */
  @PostMapping("/cancel")
  public ResponseEntity<CommonResponseDto<OrderDto>> postCancel(Authentication auth,
      @RequestBody PostOrderActionRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrderId() == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid request.", null).badRequest();
    }

    // get user id from authentication
    // in day 1 practice, use 1L instead
    // Long userid = Long.valueOf(auth.getUsername());
    Long userid = 1L;
    OrderDto result = orderService.cancelOrder(requestDto.getOrderId(), userid);
    if (result == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order not found or not in correct status.", null).ok();
    }
    return new CommonResponseDto<OrderDto>(true, 200, "", result).ok();
  }

  /**
   * Retry order, requested by user.
   * 
   * @param auth       Authentication.
   * @param requestDto Request dto.
   * @return response.
   */
  @PostMapping("/retry")
  public ResponseEntity<CommonResponseDto<OrderDto>> postRetryTicketing(Authentication auth,
      @RequestBody PostOrderActionRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrderId() == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid request.", null).badRequest();
    }

    // get user id from authentication
    // in day 1 practice, use 1L instead
    // Long userid = Long.valueOf(auth.getUsername());
    Long userid = 1L;
    OrderDto result = orderService.retryOrder(requestDto.getOrderId(), userid);
    if (result == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Order not found or not in correct status.", null).ok();
    }
    return new CommonResponseDto<OrderDto>(true, 200, "", result).ok();
  }

}
