package com.postion.airlineorderbackend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.repo.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	
	@Override
	public List<OrderDto> getAllOrders() {
		List<Order> orderList = orderRepository.findAll();
		List<OrderDto> orderDtoList = new ArrayList<OrderDto>();
		for (Order order : orderList) {
			OrderDto orderDto = new OrderDto();
			orderDto.setId(order.getId());
			orderDto.setOrderNumber(order.getOrderNumber());
			orderDto.setAmount(order.getAmount());
			orderDto.setStatus(order.getStatus());
			OrderDto.UserDto userDto = new OrderDto.UserDto();
			userDto.setId(order.getUser().getId());
			userDto.setUsername(order.getUser().getUsername());
			orderDto.setUser(userDto);
			orderDtoList.add(orderDto);
		}
		return orderDtoList;
    }
	
	@Override
	public OrderDto getOrderById(Long id) {
		Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
		OrderDto orderDto = new OrderDto();
		orderDto.setId(id);
		orderDto.setOrderNumber(order.getOrderNumber());
		orderDto.setAmount(order.getAmount());
		orderDto.setStatus(order.getStatus());
		OrderDto.UserDto userDto = new OrderDto.UserDto();
		userDto.setId(order.getUser().getId());
		userDto.setUsername(order.getUser().getUsername());
		orderDto.setUser(userDto);
		return orderDto;
    }

	@Override
	public OrderDto payOrder(Long id) {
		return new OrderDto();
	}

	@Override
	// 这是一个异步触发方法
	public void requestTicketIssuance(Long id) {
		
	}

	@Override
	public OrderDto cancelOrder(Long id) {
		return new OrderDto();
	}
}
