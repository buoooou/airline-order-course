package com.position.airline_order_course.dto;

import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private String amount;
    private Date creationDate;
    private UserDto user;
    private Map<String, Object> flightInfo;

}