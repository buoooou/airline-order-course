package com.postion.airlineorderbackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.request.TestT2RequestDto;
import com.postion.airlineorderbackend.dto.response.CommonResponseDto;
import com.postion.airlineorderbackend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

  private final OrderService orderService;

  /**
   * Test controller method for /api/test/t1
   * 
   * @return All orders
   */
  @PostMapping("/t1")
  @Operation(summary = "测试1", description = "获取所有Order")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "成功获取所有Order列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
      @ApiResponse(responseCode = "401", description = "未授权")
  })
  public List<OrderDto> getAllOrders() {
    List<OrderDto> results = orderService.getAllOrders();
    return results;
  }

  /**
   * Test controller method for /api/test/t2
   * 
   * @param requestDto Request data, sample: { "userid": 1 }
   * @return All orders of the user
   */
  @PostMapping("/t2")
  @Operation(summary = "测试2", description = "获取当前登陆User的所有Order列表")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "成功获取当前登陆User的所有Order列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))),
      @ApiResponse(responseCode = "401", description = "未授权")
  })
  public ResponseEntity<CommonResponseDto<List<OrderDto>>> getOrdersForUser(@RequestBody TestT2RequestDto requestDto) {
    CommonResponseDto<List<OrderDto>> res = new CommonResponseDto<List<OrderDto>>();
    Long userid = requestDto.getUserid();
    if (userid == null) {
      res.setSuccess(false);
      res.setMessage("Can not get user id.");
      return res.ok();
    }

    List<OrderDto> result = orderService.getAllOrdersByUserId(userid);
    if (result == null) {
      res.setSuccess(false);
      res.setMessage("Query failed.");
      return res.ok();
    }

    res.setMessage(result.size() + " order(s) found.");
    res.setData(result);
    return res.ok();
  }

  @GetMapping("/t3")
  public String getMethodName() {
    orderService.requestTicketIssuance();
    return new String();
  }

}
