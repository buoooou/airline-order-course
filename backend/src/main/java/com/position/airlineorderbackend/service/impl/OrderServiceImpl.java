package com.position.airlineorderbackend.service.impl;

import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.model.OrderStatus;
import com.position.airlineorderbackend.dto.OrderDto;
import com.position.airlineorderbackend.repo.OrderRepository;
import com.position.airlineorderbackend.service.OrderService;
import com.position.airlineorderbackend.service.OrderStateLockService;
import com.position.airlineorderbackend.exception.OrderException;
import com.position.airlineorderbackend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderStateLockService lockService;

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
        return orders.stream()
            .map(this::toDto)
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
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

    private OrderDto toDto(Order order) {
        if (order == null) return null;
        
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        dto.setCreatedDate(order.getCreatedDate());
        
        // 设置用户ID和航班信息ID
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
        }
        if (order.getFlightInfo() != null) {
            dto.setFlightInfoId(order.getFlightInfo().getId());
        }
        
        return dto;
    }
} 