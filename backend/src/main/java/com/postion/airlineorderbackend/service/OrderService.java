package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderCreateRequest;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.dto.OrderUpdateRequest;
import com.postion.airlineorderbackend.entity.FlightInfo;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.enums.OrderStatus;
import com.postion.airlineorderbackend.repository.FlightInfoRepository;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务类
 * 提供订单相关的业务逻辑处理，包括订单状态机管理
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService implements IOrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FlightInfoRepository flightInfoRepository;
    private final FlightInfoService flightInfoService;
    
    /**
     * 根据ID查找订单
     * @param id 订单ID
     * @return 订单DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findById(Long id) {
        log.debug("根据ID查找订单: {}", id);
        
        return orderRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    /**
     * 根据订单号查找订单
     * @param orderNumber 订单号
     * @return 订单DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findByOrderNumber(String orderNumber) {
        log.debug("根据订单号查找订单: {}", orderNumber);
        
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::convertToDTO);
    }
    
    /**
     * 根据用户ID查找订单列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页的订单DTO
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByUserId(Long userId, Pageable pageable) {
        log.debug("根据用户ID查找订单: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在: {}", userId);
            return Page.empty();
        }
        
        Page<Order> orders = orderRepository.findByUser(userOpt.get(), pageable);
        return orders.map(this::convertToDTO);
    }
    
    /**
     * 根据订单状态查找订单
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 分页的订单DTO
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByStatus(OrderStatus status, Pageable pageable) {
        log.debug("根据状态查找订单: {}", status);
        
        Page<Order> orders = orderRepository.findByStatus(status, pageable);
        return orders.map(this::convertToDTO);
    }
    
    /**
     * 创建新订单
     * @param createRequest 创建订单请求
     * @return 创建的订单DTO
     * @throws IllegalArgumentException 如果参数无效
     */
    public OrderDTO createOrder(OrderCreateRequest createRequest) {
        log.info("创建新订单: 用户ID={}, 航班ID={}", createRequest.getUserId(), createRequest.getFlightInfoId());
        
        // 验证用户
        User user = userRepository.findById(createRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + createRequest.getUserId()));
        
        // 验证航班
        FlightInfo flightInfo = flightInfoRepository.findById(createRequest.getFlightInfoId())
                .orElseThrow(() -> new IllegalArgumentException("航班不存在: " + createRequest.getFlightInfoId()));
        
        // 检查航班是否可预订
        if (!flightInfo.isBookable()) {
            throw new IllegalArgumentException("航班不可预订: " + flightInfo.getFlightNumber());
        }
        
        // 检查座位是否足够
        int passengerCount = createRequest.getPassengerCount() != null ? createRequest.getPassengerCount() : 1;
        if (!flightInfo.hasAvailableSeats(passengerCount)) {
            throw new IllegalArgumentException("座位不足: 需要" + passengerCount + "个座位，可用" + flightInfo.getAvailableSeats() + "个");
        }
        
        // 预订座位
        if (!flightInfoService.bookSeats(flightInfo.getId(), passengerCount)) {
            throw new IllegalArgumentException("座位预订失败");
        }
        
        try {
            // 生成订单号
            String orderNumber = generateOrderNumber();
            
            // 计算订单金额
            BigDecimal amount = flightInfo.getPrice().multiply(BigDecimal.valueOf(passengerCount));
            
            // 创建订单实体
            Order order = Order.builder()
                    .orderNumber(orderNumber)
                    .user(user)
                    .flightInfo(flightInfo)
                    .status(OrderStatus.PENDING_PAYMENT)
                    .amount(amount)
                    .passengerCount(passengerCount)
                    .passengerNames(String.join(",", createRequest.getPassengerNames()))
                    .contactPhone(createRequest.getContactPhone())
                    .contactEmail(createRequest.getContactEmail())
                    .creationDate(LocalDateTime.now())
                    .lastUpdated(LocalDateTime.now())
                    .build();
            
            Order savedOrder = orderRepository.save(order);
            log.info("订单创建成功: {}", savedOrder.getOrderNumber());
            
            return convertToDTO(savedOrder);
            
        } catch (Exception e) {
            // 如果订单创建失败，释放已预订的座位
            flightInfoService.releaseSeats(flightInfo.getId(), passengerCount);
            log.error("订单创建失败，已释放座位", e);
            throw e;
        }
    }
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param updateRequest 更新请求
     * @return 更新后的订单DTO
     * @throws IllegalArgumentException 如果订单不存在或状态转换无效
     */
    public OrderDTO updateOrderStatus(Long orderId, OrderUpdateRequest updateRequest) {
        log.info("更新订单状态: 订单ID={}, 新状态={}", orderId, updateRequest.getStatus());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        
        // 验证状态转换
        if (!isValidStatusTransition(order.getStatus(), updateRequest.getStatus())) {
            throw new IllegalArgumentException("无效的状态转换: " + order.getStatus() + " -> " + updateRequest.getStatus());
        }
        
        // 执行状态转换
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(updateRequest.getStatus());
        order.setLastUpdated(LocalDateTime.now());
        
        // 根据新状态执行相应的业务逻辑
        handleStatusChange(order, oldStatus, updateRequest);
        
        Order updatedOrder = orderRepository.save(order);
        log.info("订单状态更新成功: {} - {} -> {}", 
                updatedOrder.getOrderNumber(), oldStatus, updatedOrder.getStatus());
        
        return convertToDTO(updatedOrder);
    }
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @param reason 取消原因
     * @return 取消后的订单DTO
     */
    public OrderDTO cancelOrder(Long orderId, String reason) {
        log.info("取消订单: 订单ID={}, 原因={}", orderId, reason);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        
        // 检查订单是否可以取消
        if (!canCancelOrder(order.getStatus())) {
            throw new IllegalArgumentException("订单状态不允许取消: " + order.getStatus());
        }
        
        // 释放座位
        if (order.getFlightInfo() != null && order.getPassengerCount() != null) {
            flightInfoService.releaseSeats(order.getFlightInfo().getId(), order.getPassengerCount());
        }
        
        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setCancellationTime(LocalDateTime.now());
        order.setLastUpdated(LocalDateTime.now());
        
        Order cancelledOrder = orderRepository.save(order);
        log.info("订单取消成功: {}", cancelledOrder.getOrderNumber());
        
        return convertToDTO(cancelledOrder);
    }
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @return 支付后的订单DTO
     */
    public OrderDTO payOrder(Long orderId) {
        log.info("支付订单: 订单ID={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalArgumentException("订单状态不允许支付: " + order.getStatus());
        }
        
        // 更新订单状态为已支付
        order.setStatus(OrderStatus.PAID);
        order.setPaymentTime(LocalDateTime.now());
        order.setLastUpdated(LocalDateTime.now());
        
        Order paidOrder = orderRepository.save(order);
        log.info("订单支付成功: {}", paidOrder.getOrderNumber());
        
        return convertToDTO(paidOrder);
    }
    
    /**
     * 获取用户的活跃订单
     * @param userId 用户ID
     * @return 活跃订单DTO列表
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getActiveOrdersByUser(Long userId) {
        log.debug("获取用户活跃订单: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        
        List<Order> activeOrders = orderRepository.findActiveOrdersByUser(user);
        return activeOrders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 查找超时未支付的订单
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单DTO列表
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> findTimeoutOrders(int timeoutMinutes) {
        log.debug("查找超时未支付的订单: {}分钟", timeoutMinutes);
        
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        List<Order> timeoutOrders = orderRepository.findTimeoutOrders(timeoutTime);
        
        return timeoutOrders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 自动取消超时订单
     * @param timeoutMinutes 超时分钟数
     * @return 取消的订单数量
     */
    public int cancelTimeoutOrders(int timeoutMinutes) {
        log.info("自动取消超时订单: {}分钟", timeoutMinutes);
        
        List<OrderDTO> timeoutOrders = findTimeoutOrders(timeoutMinutes);
        int cancelledCount = 0;
        
        for (OrderDTO orderDTO : timeoutOrders) {
            try {
                cancelOrder(orderDTO.getId(), "支付超时自动取消");
                cancelledCount++;
            } catch (Exception e) {
                log.error("自动取消订单失败: {}", orderDTO.getOrderNumber(), e);
            }
        }
        
        log.info("自动取消超时订单完成: 取消{}个订单", cancelledCount);
        return cancelledCount;
    }
    
    /**
     * 多条件搜索订单
     * @param orderNumber 订单号（可为空）
     * @param userId 用户ID（可为空）
     * @param status 订单状态（可为空）
     * @param startTime 开始时间（可为空）
     * @param endTime 结束时间（可为空）
     * @param pageable 分页参数
     * @return 分页的订单DTO
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByMultipleConditions(String orderNumber, Long userId, OrderStatus status,
                                                   LocalDateTime startTime, LocalDateTime endTime,
                                                   Pageable pageable) {
        log.debug("多条件搜索订单: 订单号={}, 用户ID={}, 状态={}, 时间范围={}-{}", 
                orderNumber, userId, status, startTime, endTime);
        
        Page<Order> orders = orderRepository.findByMultipleConditions(
                orderNumber, userId, status, startTime, endTime, pageable);
        
        return orders.map(this::convertToDTO);
    }
    
    /**
     * 获取订单统计信息
     * @return 订单统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> getOrderStatistics() {
        log.debug("获取订单统计信息");
        return orderRepository.countOrdersByStatus();
    }
    
    /**
     * 生成订单号
     * @return 唯一的订单号
     */
    private String generateOrderNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ORD" + timestamp + uuid.toUpperCase();
    }
    
    /**
     * 验证订单状态转换是否有效
     * @param currentStatus 当前状态
     * @param newStatus 新状态
     * @return 是否有效
     */
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return true; // 相同状态允许
        }
        
        switch (currentStatus) {
            case PENDING_PAYMENT:
                return newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
            case PAID:
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS || newStatus == OrderStatus.CANCELLED;
            case TICKETING_IN_PROGRESS:
                return newStatus == OrderStatus.TICKETED || newStatus == OrderStatus.TICKETING_FAILED;
            case TICKETING_FAILED:
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS || newStatus == OrderStatus.CANCELLED;
            case TICKETED:
                return false; // 已出票的订单不能再变更状态
            case CANCELLED:
                return false; // 已取消的订单不能再变更状态
            default:
                return false;
        }
    }
    
    /**
     * 处理状态变更的业务逻辑
     * @param order 订单
     * @param oldStatus 旧状态
     * @param updateRequest 更新请求
     */
    private void handleStatusChange(Order order, OrderStatus oldStatus, OrderUpdateRequest updateRequest) {
        switch (updateRequest.getStatus()) {
            case PAID:
                order.setPaymentTime(LocalDateTime.now());
                break;
            case TICKETING_IN_PROGRESS:
                order.setTicketingStartTime(LocalDateTime.now());
                break;
            case TICKETED:
                order.setTicketingCompletionTime(LocalDateTime.now());
                break;
            case TICKETING_FAILED:
                order.setTicketingFailureReason(updateRequest.getReason());
                break;
            case CANCELLED:
                order.setCancellationReason(updateRequest.getReason());
                order.setCancellationTime(LocalDateTime.now());
                // 释放座位
                if (order.getFlightInfo() != null && order.getPassengerCount() != null) {
                    flightInfoService.releaseSeats(order.getFlightInfo().getId(), order.getPassengerCount());
                }
                break;
        }
    }
    
    /**
     * 检查订单是否可以取消
     * @param status 订单状态
     * @return 是否可以取消
     */
    private boolean canCancelOrder(OrderStatus status) {
        return status == OrderStatus.PENDING_PAYMENT || 
               status == OrderStatus.PAID || 
               status == OrderStatus.TICKETING_FAILED;
    }
    
    /**
     * 将Order实体转换为OrderDTO
     * @param order 订单实体
     * @return 订单DTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO.OrderDTOBuilder builder = OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .statusDescription(order.getStatus() != null ? order.getStatus().getDescription() : "")
                .amount(order.getAmount())
                .passengerCount(order.getPassengerCount())
                .passengerNames(order.getPassengerNames())
                .contactPhone(order.getContactPhone())
                .contactEmail(order.getContactEmail())
                .creationDate(order.getCreationDate())
                .lastUpdated(order.getLastUpdated())
                .paymentTime(order.getPaymentTime())
                .ticketingStartTime(order.getTicketingStartTime())
                .ticketingCompletionTime(order.getTicketingCompletionTime())
                .ticketingFailureReason(order.getTicketingFailureReason())
                .cancellationTime(order.getCancellationTime())
                .cancellationReason(order.getCancellationReason());
        
        // 设置用户信息
        if (order.getUser() != null) {
            builder.userId(order.getUser().getId())
                   .username(order.getUser().getUsername());
        }
        
        // 设置航班信息
        if (order.getFlightInfo() != null) {
            FlightInfo flight = order.getFlightInfo();
            builder.flightInfoId(flight.getId())
                   .flightNumber(flight.getFlightNumber())
                   .airline(flight.getAirline())
                   .departureAirport(flight.getDepartureAirport())
                   .arrivalAirport(flight.getArrivalAirport())
                   .departureTime(flight.getDepartureTime())
                   .arrivalTime(flight.getArrivalTime());
        }
        
        return builder.build();
    }
}
