package com.postion.airlineorderbackend.component;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.stereotype.Component;

import com.postion.airlineorderbackend.entity.Order;

/**
 * Mapper for Order ↔ OrderDto.
 */
@Component
public class OrderMapper {

    public OrderDto order2dto(Order order) {
        // Simple mapping, fill in details as per your Order/OrderDto fields
        OrderDto dto = new OrderDto();
//        dto.setId(order.getId());
//        dto.setOrderNumber(order.getOrderNumber());
//        dto.setStatus(order.getStatus());
//        dto.setCreationDate(order.getCreationDate());
        // Add more fields as needed
        return dto;
    }

    public List<OrderDto> list2dto(List<Order> orders) {
        return orders.stream().map(this::order2dto).collect(Collectors.toList());
    }
}