package com.postion.airlineorderbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.postion.airlineorderbackend.model.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {

    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private UserDto user;
//    private Map<String,Object> flightInfo;

    

}
