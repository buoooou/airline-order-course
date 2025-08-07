package com.postion.airlineorderbackend.service;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.OrderResponseDTO;
import com.postion.airlineorderbackend.entity.Flight;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.OrderStatus;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.repository.FlightRepository;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService2 {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final IOrderStateService orderStateService; // 注入状态机

    // 获取订单并返回 DTO
    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return convertToDTO(order);
    }

    // 创建新订单
    public OrderResponseDTO createOrder(String userEmail, Long flightId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        Order order = Order.builder()
                .user(user)
                .flight(flight)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();

        return convertToDTO(orderRepository.save(order));
    }

    // 更新订单状态（通过状态机校验）
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus nextStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus updatedStatus = orderStateService.updateStatus(order.getStatus(), nextStatus);
        order.setStatus(updatedStatus);

        return convertToDTO(orderRepository.save(order));
    }

    private OrderResponseDTO convertToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setFlightNumber(order.getFlight().getFlightNumber());
        dto.setStatus(order.getStatus());
        dto.setUserEmail(order.getUser().getEmail());
        return dto;
    }
}
