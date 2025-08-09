package com.postion.airlineorderbackend.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.request.PostDummyTicketingRequestDto;
import com.postion.airlineorderbackend.dto.response.CommonResponseDto;
import com.postion.airlineorderbackend.dto.response.PostDummyTicketingResponseDto;

/**
 * A dummy ticketing client.
 */
@RestController
@RequestMapping("/api/dummy")
public class DummyTicketingClient {

  /**
   * A dummy ticketing process.
   * 
   * @return Received orders.
   */
  @PostMapping("/ticket")
  public ResponseEntity<CommonResponseDto<List<Long>>> postTicketing(
      @RequestBody PostDummyTicketingRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrders() == null || requestDto.getOrders().size() == 0) {
      return new CommonResponseDto<List<Long>>(false, 200, "Nothing to process.", null).ok();
    }

    List<Long> recievedOrders = new ArrayList<Long>();
    requestDto.getOrders().forEach(order -> {
      recievedOrders.add(order.getId());
    });
    return new CommonResponseDto<List<Long>>(true, 200, "OK",
        recievedOrders).ok();
  }

  /**
   * A dummy ticketing verify process. 80% ok, 20% ng.
   * 
   * @return Ticketing result.
   */
  @PostMapping("/verify")
  public ResponseEntity<CommonResponseDto<PostDummyTicketingResponseDto>> postVerifyTicketing(
      @RequestBody PostDummyTicketingRequestDto requestDto) {
    if (requestDto == null || requestDto.getOrders() == null || requestDto.getOrders().size() == 0) {
      return new CommonResponseDto<PostDummyTicketingResponseDto>(false, 200, "Nothing to process.", null).ok();
    }

    List<Long> orderList = new ArrayList<Long>();
    List<Long> ngList = new ArrayList<Long>();
    requestDto.getOrders().forEach(order -> {
      if (ThreadLocalRandom.current().nextInt(10) < 8) {
        orderList.add(order.getId());
      } else {
        ngList.add(order.getId());
      }
    });
    return new CommonResponseDto<PostDummyTicketingResponseDto>(true, 200, "OK",
        PostDummyTicketingResponseDto.builder().ticketedOrderIDs(orderList).failedOrderIDs(ngList).build()).ok();
  }

}
