package com.postion.airlineorderbackend.dto;

import java.util.List;
import lombok.Data;

/**
 * Response DTO for dummy ticketing verification.
 */
@Data
public class PostDummyTicketingResponseDto {
    private List<Long> ticketedOrderIDs;
    private List<Long> failedOrderIDs;
}