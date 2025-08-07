package com.postion.airlineorderbackend.dto;

import java.util.List;

import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;

import lombok.Data;

/**
 * Request DTO for dummy ticketing.
 */
@Data
public class PostDummyTicketingRequestDto {
    private List<OrderDto> orders;
}