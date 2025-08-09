package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.request.PostDummyTicketingRequestDto;
import com.postion.airlineorderbackend.dto.response.CommonResponseDto;
import com.postion.airlineorderbackend.dto.response.PostDummyTicketingResponseDto;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.exception.InvalidOrderStatusException;
import com.postion.airlineorderbackend.exception.UserNotFoundException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  private final UserRepository userRepository;

  private final OrderMapper orderMapper;

  @Resource
  private RestTemplate restTemplate;

  @Override
  public List<OrderDto> getAllOrders() {
    List<Order> allOrders = orderRepository.findAll();
    List<OrderDto> rtn = new ArrayList<OrderDto>();
    for (Order order : allOrders) {
      rtn.add(orderMapper.order2dto(order));
    }
    return rtn;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto getOrderById(Long id) throws DataNotFoundException {
    Optional<Order> order = orderRepository.findById(id);
    if (order.isPresent()) {
      return orderMapper.order2dto(order.get());
    }
    throw new DataNotFoundException();
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto payOrder(Long orderId, Long userId)
      throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (!orderOptional.isPresent()) {
      throw new DataNotFoundException();
    }
    Order order = orderOptional.get();
    if (!order.getUser().getId().equals(userId)) {
      throw new UserNotFoundException();
    }
    if (!order.getStatus().equals(OrderStatus.PENDING_PAYMENT)) {
      throw new InvalidOrderStatusException();
    }
    order.setStatus(OrderStatus.PAID);
    return orderMapper.order2dto(order);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto cancelOrder(Long orderId, Long userId)
      throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (!orderOptional.isPresent()) {
      throw new DataNotFoundException();
    }
    Order order = orderOptional.get();
    if (!order.getUser().getId().equals(userId)) {
      throw new UserNotFoundException();
    }
    if (!(order.getStatus().equals(OrderStatus.PENDING_PAYMENT) || order.getStatus()
        .equals(OrderStatus.PAID) || order.getStatus().equals(OrderStatus.TICKETING_FAILED))) {
      throw new InvalidOrderStatusException();
    }
    order.setStatus(OrderStatus.CANCELLED);
    return orderMapper.order2dto(order);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto retryOrder(Long orderId, Long userId)
      throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (!orderOptional.isPresent()) {
      throw new DataNotFoundException();
    }
    Order order = orderOptional.get();
    if (!order.getUser().getId().equals(userId)) {
      throw new UserNotFoundException();
    }
    if (!order.getStatus().equals(OrderStatus.TICKETING_FAILED)) {
      throw new InvalidOrderStatusException();
    }
    order.setStatus(OrderStatus.PAID); // ? change to paid for processing ticketing
    return orderMapper.order2dto(order);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public OrderDto updateOrderStatus(Long id, OrderStatus status) throws DataNotFoundException {
    Optional<Order> result = orderRepository.findById(id);
    if (!result.isPresent()) {
      throw new DataNotFoundException();
    }
    Order order = result.get();
    order.setStatus(status);
    Order updatedOrder = orderRepository.save(order);
    OrderDto dto = orderMapper.order2dto(updatedOrder);
    return dto;
  };

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public void requestTicketIssuance() {
    // targeting PAID orders.
    // skip further actions if no target found
    List<Order> orders = orderRepository.findByStatus(OrderStatus.PAID);
    if (orders.isEmpty()) {
      return;
    }

    // request ticketing
    PostDummyTicketingRequestDto requestDto = new PostDummyTicketingRequestDto();
    requestDto.setOrders(orderMapper.list2dto(orders));
    HttpEntity<PostDummyTicketingRequestDto> request = new HttpEntity<PostDummyTicketingRequestDto>(requestDto);
    @SuppressWarnings("unchecked")
    CommonResponseDto<List<Long>> res = (CommonResponseDto<List<Long>>) restTemplate
        .postForObject(
            "http://127.0.0.1:8080/api/dummy/ticket",
            request, CommonResponseDto.class);
    if (res == null || !res.isSuccess()) {
      return;
    }
    orders.forEach(order -> {
      if (res.getData().contains(order.getId())) {
        order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        orderRepository.save(order);
      }
    });
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public void verifyTicketIssuance() {
    // targeting TICKETING_IN_PROGRESS orders.
    // skip further actions if no target found
    List<Order> orders = orderRepository.findByStatus(OrderStatus.TICKETING_IN_PROGRESS);
    if (orders.isEmpty()) {
      return;
    }

    // verify ticketing
    PostDummyTicketingRequestDto requestDto = new PostDummyTicketingRequestDto();
    requestDto.setOrders(orderMapper.list2dto(orders));
    HttpEntity<PostDummyTicketingRequestDto> request = new HttpEntity<PostDummyTicketingRequestDto>(requestDto);
    @SuppressWarnings("unchecked")
    CommonResponseDto<PostDummyTicketingResponseDto> res = (CommonResponseDto<PostDummyTicketingResponseDto>) restTemplate
        .postForObject(
            "http://127.0.0.1:8080/api/dummy/verify",
            request, CommonResponseDto.class);
    if (res == null || !res.isSuccess()) {
      return;
    }
    orders.forEach(order -> {
      if (res.getData().getTicketedOrderIDs().contains(order.getId())) {
        order.setStatus(OrderStatus.TICKETED);
        orderRepository.save(order);
      } else if (res.getData().getFailedOrderIDs().contains(order.getId())) {
        order.setStatus(OrderStatus.TICKETING_FAILED);
        orderRepository.save(order);
      }
    });
  }

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
    return orderMapper.list2dto(result);
  };

}
