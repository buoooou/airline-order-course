package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  UserRepository userRepository;

  @Override
  public List<OrderDto> getAllOrders() {
    List<Order> allOrders = orderRepository.findAll();
    List<OrderDto> rtn = new ArrayList<OrderDto>();
    for (Order order : allOrders) {
      rtn.add(OrderMapper.order2dto(order));
    }
    return rtn;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto getOrderById(Long id) {
    Optional<Order> order = orderRepository.findById(id);
    if (order.isPresent()) {
      return OrderMapper.order2dto(order.get());
    }
    return null;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto payOrder(Long orderId, Long userId) {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (!orderOptional.isPresent()) {
      return null;
    }
    Order order = orderOptional.get();
    if (!order.getUser().getId().equals(userId)) {
      return null;
    }
    if (!order.getStatus().equals(OrderStatus.PENDING_PAYMENT)) {
      return null;
    }
    order.setStatus(OrderStatus.PAID);
    return OrderMapper.order2dto(order);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto cancelOrder(Long orderId, Long userId) {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (!orderOptional.isPresent()) {
      return null;
    }
    Order order = orderOptional.get();
    if (!order.getUser().getId().equals(userId)) {
      return null;
    }
    if (!(order.getStatus().equals(OrderStatus.PENDING_PAYMENT) || order.getStatus()
        .equals(OrderStatus.PAID) || order.getStatus().equals(OrderStatus.TICKETING_FAILED))) {
      return null;
    }
    order.setStatus(OrderStatus.CANCELLED);
    return OrderMapper.order2dto(order);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto retryOrder(Long orderId, Long userId) {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (!orderOptional.isPresent()) {
      return null;
    }
    Order order = orderOptional.get();
    if (!order.getUser().getId().equals(userId)) {
      return null;
    }
    if (!order.getStatus().equals(OrderStatus.TICKETING_FAILED)) {
      return null;
    }
    order.setStatus(OrderStatus.PAID); // ? change to paid for processing ticketing
    return OrderMapper.order2dto(order);
  }

  @Override
  public void requestTicketIssuance(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'requestTicketIssuance'");
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto updateOrderStatus(Long id, OrderStatus status) {
    Optional<Order> result = orderRepository.findById(id);
    if (!result.isPresent()) {
      return null;
    }
    Order order = result.get();
    order.setStatus(status);
    Order updatedOrder = orderRepository.save(order);
    OrderDto dto = OrderMapper.order2dto(updatedOrder);
    return dto;
  };

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public void cancelPaymentExpiredTickets() {
    // targeting PENDING_PAYMENT for 30minutes,
    // skip further actions if no target found
    LocalDateTime timeExpire = LocalDateTime.now().minusMinutes(30);
    List<Order> orders = orderRepository.findByStatusAndCreationDateBefore(OrderStatus.PENDING_PAYMENT,
        timeExpire);
    if (orders.isEmpty()) {
      return;
    }

    // update status
    LocalDateTime timeNow = LocalDateTime.now();
    for (Order order : orders) {
      order.setStatus(OrderStatus.CANCELLED);
      order.setCreationDate(timeNow);
      System.out.println(order.getOrderNumber());
    }

    // save updates to db
    orderRepository.saveAll(orders);
  }

  @Override
  public List<OrderDto> getAllOrdersByUserId(Long id) {
    List<Order> result = userRepository.findOrdersByUserId(id);
    return OrderMapper.list2dto(result);
  };

}
