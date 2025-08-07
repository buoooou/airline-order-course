package com.postion.airlineorderbackend.service.impl;


import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final AirlineApiClient airlineApiClient;

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = orderMapper.toEntity(orderDto);
        order.setId(null); // 确保是新建
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        
        // 处理用户关系
        if (orderDto.getUser() != null && orderDto.getUser().getId() != null) {
            userRepository.findById(orderDto.getUser().getId()).ifPresent(order::setUser);
        }
        
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        // 使用MapStruct更新实体
        orderMapper.updateOrderFromDto(orderDto, order);
        
        // 用户更新（如有需要）- 因为在mapper中忽略了user字段
        if (orderDto.getUser() != null && orderDto.getUser().getId() != null) {
            userRepository.findById(orderDto.getUser().getId())
                .ifPresent(order::setUser);
        }
        
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public OrderDto payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order is not pending payment");
        }
        order.setStatus(OrderStatus.PAID);
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void requestTicketIssuance(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Order must be in PAID status to request ticket issuance");
        }
        
        // 更新订单状态为出票中
        order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        orderRepository.save(order);
        
        // 异步调用航空公司API出票
        new Thread(() -> {
            try {
                // 调用航空公司API出票
                String ticketNumber = airlineApiClient.issueTicket(order.getId());
                // 处理出票成功
                handleTicketIssuanceSuccess(order.getId(), ticketNumber);
            } catch (Exception e) {
                // 处理出票失败
                log.error("出票失败: {}", e.getMessage());
                handleTicketIssuanceFailure(order.getId(), e.getMessage());
            }
        }).start();
    }
    
    /**
     * 处理出票成功
     * 
     * @param orderId 订单ID
     * @param ticketNumber 票号
     */
    @Transactional
    public void handleTicketIssuanceSuccess(Long orderId, String ticketNumber) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            // 只有当订单状态为出票中时才更新
            if (order.getStatus() == OrderStatus.TICKETING_IN_PROGRESS) {
                log.info("订单 {} 出票成功，票号: {}", order.getOrderNumber(), ticketNumber);
                order.setStatus(OrderStatus.TICKETED);
                order.setTicketNumber(ticketNumber); // 保存票号
                orderRepository.save(order);
            }
        } catch (Exception e) {
            log.error("处理出票成功时发生错误: {}", e.getMessage());
        }
    }
    
    /**
     * 处理出票失败
     * 
     * @param orderId 订单ID
     * @param errorMessage 错误信息
     */
    @Transactional
    public void handleTicketIssuanceFailure(Long orderId, String errorMessage) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            // 只有当订单状态为出票中时才更新
            if (order.getStatus() == OrderStatus.TICKETING_IN_PROGRESS) {
                log.info("订单 {} 出票失败: {}", order.getOrderNumber(), errorMessage);
                order.setStatus(OrderStatus.TICKETING_FAILED);
                orderRepository.save(order);
                // 这里可以添加通知逻辑，如发送邮件或短信通知用户出票失败
            }
        } catch (Exception e) {
            log.error("处理出票失败时发生错误: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }
        
        // 如果订单已出票，尝试退票
        if (order.getStatus() == OrderStatus.TICKETED && order.getTicketNumber() != null) {
            try {
                boolean refundSuccess = airlineApiClient.refundTicket(order.getTicketNumber());
                if (!refundSuccess) {
                    throw new IllegalStateException("Failed to refund ticket");
                }
                log.info("订单 {} 退票成功", order.getOrderNumber());
            } catch (Exception e) {
                log.error("订单 {} 退票失败: {}", order.getOrderNumber(), e.getMessage());
                throw new IllegalStateException("Failed to refund ticket: " + e.getMessage());
            }
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }
    
    /**
     * 查询票务状态
     * 
     * @param orderId 订单ID
     * @return 票务状态
     */
    public String queryTicketStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        if (order.getStatus() != OrderStatus.TICKETED || order.getTicketNumber() == null) {
            throw new IllegalStateException("Order is not ticketed");
        }
        
        try {
            return airlineApiClient.queryTicketStatus(order.getTicketNumber());
        } catch (Exception e) {
            log.error("查询票务状态失败: {}", e.getMessage());
            throw new RuntimeException("Failed to query ticket status: " + e.getMessage());
        }
    }

    /**
     * 处理超时未支付的订单
     * 每5分钟执行一次，锁定至少4分钟，最多10分钟
     */
    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(name = "processUnpaidOrders", lockAtLeastFor = "PT4M", lockAtMostFor = "PT10M")
    @Transactional
    public void processUnpaidOrders() {
        log.info("开始处理超时未支付订单...");
        try {
            // 查找所有待支付状态且创建时间超过30分钟的订单
            List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDateBefore(
                    OrderStatus.PENDING_PAYMENT,
                    LocalDateTime.now().minusMinutes(30)
            );
            
            log.info("找到{}个超时未支付订单", unpaidOrders.size());
            
            for (Order order : unpaidOrders) {
                log.info("取消超时未支付订单: {}", order.getOrderNumber());
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            }
            
            log.info("超时未支付订单处理完成");
        } catch (Exception e) {
            log.error("处理超时未支付订单时发生错误", e);
        }
    }

    /**
     * 处理出票中的订单
     * 每10分钟执行一次，锁定至少9分钟，最多15分钟
     */
    @Scheduled(cron = "0 */10 * * * *")
    @SchedulerLock(name = "processTicketingOrders", lockAtLeastFor = "PT9M", lockAtMostFor = "PT15M")
    @Transactional
    public void processTicketingOrders() {
        log.info("开始处理出票中订单...");
        try {
            // 查找所有出票中状态且创建时间超过1小时的订单
            List<Order> ticketingOrders = orderRepository.findByStatusAndCreationDateBefore(
                    OrderStatus.TICKETING_IN_PROGRESS,
                    LocalDateTime.now().minusHours(1)
            );
            
            log.info("找到{}个长时间处于出票中的订单", ticketingOrders.size());
            
            for (Order order : ticketingOrders) {
                log.info("将长时间出票中订单标记为出票失败: {}", order.getOrderNumber());
                order.setStatus(OrderStatus.TICKETING_FAILED);
                orderRepository.save(order);
                // 这里可以添加通知逻辑，如发送邮件或短信通知用户出票失败
            }
            
            log.info("出票中订单处理完成");
        } catch (Exception e) {
            log.error("处理出票中订单时发生错误", e);
        }
    }
}
