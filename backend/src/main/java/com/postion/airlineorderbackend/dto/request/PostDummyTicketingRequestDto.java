package com.postion.airlineorderbackend.dto.request;

import java.util.List;

import com.postion.airlineorderbackend.dto.OrderDto;

import lombok.Data;

@Data
public class PostDummyTicketingRequestDto {

  private List<OrderDto> orders;

}
