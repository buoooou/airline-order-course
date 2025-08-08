package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException("USER_INVALID", "当前用户无效");
        }
        return orderRepository.findByUserId(currentUser.getId());
    }

    @Override
    public Order getOrderById(Long id) {
        if (id == null) {
            throw new BusinessException("ORDER_ID_REQUIRED", "订单ID不能为空");
        }
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "订单未找到"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order) {
        if (order == null) {
            throw new BusinessException("ORDER_REQUIRED", "订单不能为空");
        }
        if (order.getAmount() == null || order.getAmount().compareTo(new BigDecimal("0.01")) < 0) {
            throw new BusinessException("ORDER_AMOUNT_INVALID", "订单金额必须大于0");
        }
        if (order.getUser() == null || order.getUser().getId() == null) {
            throw new BusinessException("ORDER_USER_REQUIRED", "订单所属用户不能为空");
        }
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreationDate(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Order payOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.TICKETED) {
            return order; // 幂等：已支付/已出票视为成功
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("ORDER_CANNOT_PAY", "订单无法支付");
        }
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return order; // 幂等：已取消视为成功
        }
        if (order.getStatus() == OrderStatus.TICKETED) {
            throw new BusinessException("ORDER_CANNOT_CANCEL", "订单无法取消");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Order retryTicketing(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() == OrderStatus.TICKETING_IN_PROGRESS) {
            return order; // 幂等：进行中直接返回
        }
        if (order.getStatus() != OrderStatus.TICKETING_FAILED) {
            throw new BusinessException("ORDER_CANNOT_RETRY", "订单无法重试出票");
        }
        order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        if (status == null) {
            throw new BusinessException("STATUS_REQUIRED", "状态不能为空");
        }
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order updateOrder(Order order) {
        if (order == null || order.getId() == null) {
            throw new BusinessException("ORDER_ID_REQUIRED", "订单ID不能为空");
        }
        // 确保存在
        getOrderById(order.getId());
        return orderRepository.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        if (id == null) {
            throw new BusinessException("ORDER_ID_REQUIRED", "订单ID不能为空");
        }
        // 存在性检查
        getOrderById(id);
        orderRepository.deleteById(id);
    }
} 