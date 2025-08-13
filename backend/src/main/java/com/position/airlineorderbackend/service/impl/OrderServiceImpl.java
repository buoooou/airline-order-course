package com.position.airlineorderbackend.service.impl;

import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.model.OrderStatus;
import com.position.airlineorderbackend.model.User;
import com.position.airlineorderbackend.model.FlightInfo;
import com.position.airlineorderbackend.dto.OrderDto;
import com.position.airlineorderbackend.dto.CreateOrderRequest;
import com.position.airlineorderbackend.mapper.OrderMapper;
import com.position.airlineorderbackend.repo.OrderRepository;
import com.position.airlineorderbackend.repo.UserRepository;
import com.position.airlineorderbackend.repo.FlightInfoRepository;
import com.position.airlineorderbackend.service.OrderService;
import com.position.airlineorderbackend.service.OrderStateLockService;
import com.position.airlineorderbackend.exception.OrderException;
import com.position.airlineorderbackend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FlightInfoRepository flightInfoRepository;
    
    @Autowired
    private OrderStateLockService lockService;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public String payOrder(Long id) {
        return lockService.executeWithLock(id, () -> 
            updateStatus(id, OrderStatus.PAID, "订单支付成功")
        );
    }

    @Override
    public String startTicketing(Long id) {
        return lockService.executeWithLock(id, () -> 
            updateStatus(id, OrderStatus.TICKETING_IN_PROGRESS, "开始出票处理")
        );
    }

    @Override
    public String completeTicketing(Long id) {
        return lockService.executeWithLock(id, () -> 
            updateStatus(id, OrderStatus.TICKETED, "出票完成")
        );
    }

    @Override
    public String failTicketing(Long id) {
        return lockService.executeWithLock(id, () -> 
            updateStatus(id, OrderStatus.TICKETING_FAILED, "出票失败状态已更新")
        );
    }

    @Override
    public String cancelOrder(Long id) {
        return lockService.executeWithLock(id, () -> 
            updateStatus(id, OrderStatus.CANCELLED, "订单已取消")
        );
    }

    @Override
    public String retryPayment(Long id) {
        return lockService.executeWithLock(id, () -> 
            updateStatus(id, OrderStatus.PAID, "重新支付成功，可重新出票")
        );
    }

    @Override
    public String retryTicketing(Long id) {
        return lockService.executeWithLock(id, () -> 
            updateStatus(id, OrderStatus.TICKETING_IN_PROGRESS, "重新出票处理中")
        );
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDtoList(orders);
    }

    @Override
    public List<OrderDto> getOrdersForUser(Long userId) {
        List<Order> orders = orderRepository.findByUser_Id(userId);
        return orderMapper.toDtoList(orders);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("订单", "ID", id));
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request, Long userId) {
        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "ID", userId));
        
        // 验证航班信息是否存在
        FlightInfo flightInfo = flightInfoRepository.findById(request.getFlightInfoId())
                .orElseThrow(() -> new ResourceNotFoundException("航班信息", "ID", request.getFlightInfoId()));
        
        // 创建订单
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setAmount(request.getAmount());
        order.setUser(user);
        order.setFlightInfo(flightInfo);
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        
        return orderMapper.toDto(savedOrder);
    }

    /**
     * 生成订单号
     * 格式：ORD-YYYYMMDD-HHMMSS-XXXX
     */
    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String randomSuffix = String.format("%04d", (int)(Math.random() * 10000));
        return "ORD-" + timestamp + "-" + randomSuffix;
    }

    @Transactional
    private String updateStatus(Long orderId, OrderStatus newStatus, String successMessage) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("订单", "ID", orderId));
        
        OrderStatus currentStatus = order.getStatus();
        
        // 状态机核心逻辑
        if (isValidStatusTransition(currentStatus, newStatus)) {
            order.setStatus(newStatus);
            orderRepository.save(order);
            return successMessage;
        } else {
            throw new OrderException(
                String.format("非法状态转换: %s -> %s", currentStatus, newStatus)
            );
        }
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING_PAYMENT:
                // 待支付状态只能转为：已支付、已取消
                return newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
                
            case PAID:
                // 已支付状态只能转为：出票中、已取消
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS || newStatus == OrderStatus.CANCELLED;
                
            case TICKETING_IN_PROGRESS:
                // 出票中状态只能转为：已出票、出票失败、已取消
                return newStatus == OrderStatus.TICKETED || 
                       newStatus == OrderStatus.TICKETING_FAILED || 
                       newStatus == OrderStatus.CANCELLED;
                
            case TICKETING_FAILED:
                // 出票失败状态可以转为：已支付（重新支付）、出票中（重新出票）、已取消
                return newStatus == OrderStatus.PAID || 
                       newStatus == OrderStatus.TICKETING_IN_PROGRESS || 
                       newStatus == OrderStatus.CANCELLED;
                
            case TICKETED:
                // 已出票状态是终态，不能再转换
                return false;
                
            case CANCELLED:
                // 已取消状态是终态，不能再转换
                return false;
                
            default:
                return false;
        }
    }

    // 使用 MapStruct 统一 DTO 映射
} 