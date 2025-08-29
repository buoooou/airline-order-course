package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.client.AirlineApiClient;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.CreateOrderRequest;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AirlineApiClient airlineApiClient;

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDto> list = new ArrayList<>();
        for (Order o : orders) {
            OrderDto dto = orderMapper.toDto(o);
            dto.setFlightInfo(mockFlightInfo(o));
            list.add(dto);
        }
        return list;
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        OrderDto dto = orderMapper.toDto(order);
        dto.setFlightInfo(mockFlightInfo(order));
        return dto;
    }

    @Override
    @Transactional
    public OrderDto payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Only PENDING_PAYMENT orders can be paid");
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public void requestTicketIssuance(Long id) {
        // 将状态置为出票中，然后异步调用模拟航司接口
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.TICKETING_FAILED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Order must be PAID or TICKETING_FAILED to issue");
        }
        order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        orderRepository.save(order);
        issueTicketAsync(order.getId());
    }

    @Async
    protected CompletableFuture<Void> issueTicketAsync(Long orderId) {
        try {
            String ticketNo = airlineApiClient.issueTicket(orderId);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            order.setStatus(OrderStatus.TICKETED);
            orderRepository.save(order);
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            order.setStatus(OrderStatus.TICKETING_FAILED);
            orderRepository.save(order);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        if (order.getStatus() == OrderStatus.TICKETED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "TICKETED orders cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    // ===== Helpers =====
    private Map<String, Object> mockFlightInfo(Order order) {
        Map<String, Object> info = new HashMap<>();
        info.put("carrier", "Demo Air");
        info.put("flightNo", "DA" + String.format("%03d", order.getId() == null ? 1 : order.getId()));
        info.put("from", "PEK");
        info.put("to", "SHA");
        info.put("etd", LocalDateTime.now().plusDays(7).withHour(10).withMinute(0).toString());
        info.put("eta", LocalDateTime.now().plusDays(7).withHour(12).withMinute(0).toString());
        return info;
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request, String username) {
        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));

        // 创建订单对象
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING_PAYMENT); // 初始状态为待支付
        order.setAmount(request.getAmount());
        order.setCreationDate(LocalDateTime.now());
        order.setUser(user);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 转换为DTO并返回
        OrderDto orderDto = orderMapper.toDto(savedOrder);
        orderDto.setFlightInfo(mockFlightInfo(savedOrder));
        return orderDto;
    }

    // 生成订单号的方法
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

}
