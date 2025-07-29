package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.service.OrderService;
import com.postion.airlineorderbackend.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getCurrentUserOrders(User currentUser) {
        return orderRepository.findByUserId(currentUser.getId());
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "订单未找到"));
    }

    @Override
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreationDate(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public Order payOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("ORDER_CANNOT_PAY", "订单无法支付");
        }
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() == OrderStatus.TICKETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("ORDER_CANNOT_CANCEL", "订单无法取消");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public Order retryTicketing(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.TICKETING_FAILED) {
            throw new BusinessException("ORDER_CANNOT_RETRY", "订单无法重试出票");
        }
        order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
} 