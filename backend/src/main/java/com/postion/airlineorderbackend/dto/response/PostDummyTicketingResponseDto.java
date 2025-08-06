package com.postion.airlineorderbackend.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDummyTicketingResponseDto {

  List<Long> ticketedOrderIDs;

  List<Long> failedOrderIDs;

}
