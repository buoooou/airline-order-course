package com.postion.airlineorderbackend.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService{
    
  private final OrderRepository orderRepository;

  public OrderServiceImpl (OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }
  public List<OrderDto> getAllOrders() {
  List<OrderDto> orderDtoList  = new ArrayList<>();
    for (int i = 0; i < orderRepository.findAll().size(); i++) {
        orderDtoList.add(mappOrderDto(orderRepository.findAll().get(i)));
    }
    return orderDtoList;
  }
  
   public OrderDto getOrderById(Long id) {
    Order order = orderRepository.findById(id).orElse(null);
    if(order!= null) {
        return mappOrderDto(order);
    }
    return null;
   }
   
   public OrderDto payOrder(Long id) {
     Order order = orderRepository.findById(id).orElse(null);
     if(order!= null) {
       order.setStatus(OrderStatus.PAID);
       orderRepository.save(order);
       return mappOrderDto(order);
     }
     return null;
   }
   
  public void retryTicketingIssuance(Long id) {
    Order order = orderRepository.findById(id).orElse(null);
    if(order!= null) {
      order.setStatus(OrderStatus.TICKETING_FAILED);
      orderRepository.save(order);
    }
  }
  
   public OrderDto cancelOrder(Long id) {
     Order order = orderRepository.findById(id).orElse(null);
     if(order!= null) {
        order.setStatus(OrderStatus.CANCELLED);
       orderRepository.save(order);
       return mappOrderDto(order);
     }
     return null;
   }
  
  public OrderDto mappOrderDto(Order order){
	  OrderDto orderDto = new OrderDto();
	  orderDto.setId(order.getId());
	  orderDto.setOrderNumber(order.getOrderNumber());
	  orderDto.setStatus(order.getStatus());
	  orderDto.setAmount(order.getAmount());
	  orderDto.setCreationDate(order.getCreationDate());
	  orderDto.setUser(new OrderDto.UserDto());
	  orderDto.getUser().setUserName(order.getUser().getUsername());
	  orderDto.getUser().setId(order.getUser().getId());
	  return orderDto;
  }
  
}
