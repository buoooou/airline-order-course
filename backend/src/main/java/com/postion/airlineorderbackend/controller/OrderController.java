package com.postion.airlineorderbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.request.GetOrderByIdRequestDto;
import com.postion.airlineorderbackend.dto.response.CommonResponseDto;
import com.postion.airlineorderbackend.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  /**
   * Get all orders of current user.
   * 
   * @param auth Authentication.
   * @return response.
   */
  @PostMapping("/getOfUser")
  public ResponseEntity<CommonResponseDto<List<OrderDto>>> postGetOrdersOfUser(Authentication auth) {
    // get user id from authentication
    // in day 1 practice, use 1L instead
    // Long userid = Long.valueOf(auth.getUsername());
    Long userid = 1L;

    List<OrderDto> orders = orderService.getAllOrdersByUserId(userid);
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
  public ResponseEntity<CommonResponseDto<OrderDto>> postGetOrderById(@RequestBody GetOrderByIdRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrderId() == null) {
      return new CommonResponseDto<OrderDto>(false, 200, "Invalid request.", null).badRequest();
    }

    CommonResponseDto<OrderDto> res = new CommonResponseDto<OrderDto>();
    OrderDto order = orderService.getOrderById(requestDto.getOrderId());
    if (order == null) {
      res.setSuccess(false);
      res.setMessage("Order not found.");
      res.setCode(200);
      res.setData(null);
    } else {
      res.setSuccess(true);
      res.setMessage("");
      res.setCode(200);
      res.setData(order);
    }
    return res.ok();
  }

}
