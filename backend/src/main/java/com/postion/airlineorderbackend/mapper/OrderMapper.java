package com.postion.airlineorderbackend.mapper;

import java.util.ArrayList;
import java.util.List;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;

public class OrderMapper {

  public static OrderDto order2dto(Order order) {
    OrderDto dto = new OrderDto();
    dto.setAmount(order.getAmount());
    dto.setCreationDate(order.getCreationDate());
    dto.setFlightInfo(null);
    dto.setId(order.getId());
    dto.setOrderNumber(order.getOrderNumber());
    dto.setStatus(order.getStatus());
    dto.setUser(UserMapper.user2dto(order.getUser()));
    return dto;
  }

  public static List<OrderDto> list2dto(List<Order> orders) {
    if (orders == null) {
      return null;
    }
    List<OrderDto> dtos = new ArrayList<OrderDto>();
    for (Order order : orders) {
      dtos.add(order2dto(order));
    }
    return dtos;
  }

}
