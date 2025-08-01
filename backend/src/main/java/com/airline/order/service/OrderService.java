package com.airline.order.service;

import com.airline.order.dto.CreateOrderRequest;
import com.airline.order.dto.OrderDTO;
import com.airline.order.entity.FlightInfo;
import com.airline.order.entity.Order;
import com.airline.order.enums.OrderStatus;
import com.airline.order.entity.User;
import com.airline.order.repository.FlightInfoRepository;
import com.airline.order.repository.OrderRepository;
import com.airline.order.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 订单服务类
 * 处理订单相关的业务逻辑
 */
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FlightInfoRepository flightInfoRepository;
    
    /**
     * 创建新订单
     * @param request 创建订单请求
     * @return 创建的订单DTO
     * @throws RuntimeException 当验证失败时抛出异常
     */
    public OrderDTO createOrder(CreateOrderRequest request) {
        // 验证用户是否存在
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isPresent()) {// 验证航班是否存在
            Optional<FlightInfo> flightOptional = flightInfoRepository.findById(request.getFlightId());
            if (flightOptional.isPresent()) {// 检查座位是否已被预订
                Optional<Order> existingOrder = orderRepository.findByFlightIdAndSeatNumber(
                        request.getFlightId(), request.getSeatNumber());

                if (existingOrder.isPresent()) {
                    throw new RuntimeException("座位已被预订");
                }

                // 创建订单
                Order order = new Order();
                order.setOrderNumber(generateOrderNumber());
                order.setUser(userOptional.get());
                order.setFlightInfo(flightOptional.get());
                order.setSeatNumber(request.getSeatNumber());
                order.setAmount(request.getAmount());
                order.setStatus(OrderStatus.PENDING_PAYMENT);
                order.setCreationDate(LocalDateTime.now());

                Order savedOrder = orderRepository.save(order);

                return convertToDTO(savedOrder);
            } else {
                throw new RuntimeException("航班不存在");
            }

        } else {
            throw new RuntimeException("用户不存在");
        }

    }
    
    /**
     * 查询订单列表
     * @param userId 用户ID（可选）
     * @param status 订单状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 订单分页结果
     */
    public Page<OrderDTO> getOrders(Long userId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Order> orderPage;
        
        if (userId != null && status != null) {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            orderPage = orderRepository.findByUserIdAndStatus(userId, orderStatus, pageable);
        } else if (userId != null) {
            orderPage = orderRepository.findByUserId(userId, pageable);
        } else if (status != null) {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            orderPage = orderRepository.findByStatus(orderStatus, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }
        
        return orderPage.map(this::convertToDTO);
    }
    
    /**
     * 根据ID获取订单详情
     * @param orderId 订单ID
     * @return 订单DTO
     * @throws RuntimeException 当订单不存在时抛出异常
     */
    public OrderDTO getOrderById(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isPresent()) {
            return convertToDTO(orderOptional.get());
        }
        throw new RuntimeException("订单不存在");

    }
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消后的订单DTO
     * @throws RuntimeException 当订单不存在或状态不允许取消时抛出异常
     */
    public OrderDTO cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            // 检查订单状态是否可以取消
            if (OrderStatus.CANCELLED.equals(order.getStatus()) || OrderStatus.TICKETED.equals(order.getStatus())) {
                throw new RuntimeException("订单状态不允许取消");
            }

            order.setStatus(OrderStatus.CANCELLED);
            Order savedOrder = orderRepository.save(order);

            return convertToDTO(savedOrder);
        } else {
            throw new RuntimeException("订单不存在");
        }

    }
    
    /**
     * 修改订单状态
     * @param orderId 订单ID
     * @param newStatusStr 新状态字符串
     * @return 修改后的订单DTO
     * @throws RuntimeException 当订单不存在或状态无效时抛出异常
     */
    public OrderDTO updateOrderStatus(Long orderId, String newStatusStr) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            // 验证状态值
            OrderStatus newStatus;
            try {
                newStatus = OrderStatus.valueOf(newStatusStr);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的订单状态");
            }

            order.setStatus(newStatus);
            Order savedOrder = orderRepository.save(order);

            return convertToDTO(savedOrder);
        } else {
            throw new RuntimeException("订单不存在");
        }

    }
    
    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 订单DTO
     * @throws RuntimeException 当订单不存在时抛出异常
     */
    public OrderDTO getOrderByNumber(String orderNumber) {
        Optional<Order> orderOptional = orderRepository.findByOrderNumber(orderNumber);

        if (orderOptional.isPresent()) {
            return convertToDTO(orderOptional.get());
        }
        throw new RuntimeException("订单不存在");

    }
    
    /**
     * 将Order实体转换为OrderDTO
     * @param order 订单实体
     * @return 订单DTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        dto.setCreationDate(order.getCreationDate());
        dto.setSeatNumber(order.getSeatNumber());
        
        // 设置用户信息
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUsername(order.getUser().getUsername());
        }
        
        // 设置航班信息
        if (order.getFlightInfo() != null) {
            dto.setFlightId(order.getFlightInfo().getId());
            dto.setFlightNumber(order.getFlightInfo().getFlightNumber());
        }
        
        return dto;
    }
    
    /**
     * 根据航班ID查询订单列表
     * @param flightId 航班ID
     * @return 订单DTO列表
     */
    public List<OrderDTO> getOrdersByFlightId(Long flightId) {
        List<Order> orders = orderRepository.findByFlightId(flightId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 根据用户名查询订单列表
     * @param username 用户名
     * @return 订单DTO列表
     */
    public List<OrderDTO> getOrdersByUsername(String username) {
        List<Order> orders = orderRepository.findByUsername(username);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 根据航班号查询订单列表
     * @param flightNumber 航班号
     * @return 订单DTO列表
     */
    public List<OrderDTO> getOrdersByFlightNumber(String flightNumber) {
        List<Order> orders = orderRepository.findByFlightNumber(flightNumber);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 根据座位号查询订单列表
     * @param seatNumber 座位号
     * @return 订单DTO列表
     */
    public List<OrderDTO> getOrdersBySeatNumber(String seatNumber) {
        List<Order> orders = orderRepository.findBySeatNumber(seatNumber);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 根据创建时间范围查询订单列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单DTO列表
     */
    public List<OrderDTO> getOrdersByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<Order> orders = orderRepository.findByCreationDateBetween(startTime, endTime);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 根据金额范围查询订单列表
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 订单DTO列表
     */
    public List<OrderDTO> getOrdersByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        List<Order> orders = orderRepository.findByAmountBetween(minAmount, maxAmount);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 统计指定状态的订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    /**
     * 统计指定用户的订单数量
     * @param userId 用户ID
     * @return 订单数量
     */
    public long countOrdersByUserId(Long userId) {
        return orderRepository.countByUserId(userId);
    }
    
    /**
     * 查找过期的待支付订单
     * @param expiredTime 过期时间
     * @return 过期订单DTO列表
     */
    public List<OrderDTO> getExpiredPendingPaymentOrders(LocalDateTime expiredTime) {
        List<Order> orders = orderRepository.findExpiredPendingPaymentOrders(expiredTime);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 查找出票失败的订单
     * @return 出票失败订单DTO列表
     */
    public List<OrderDTO> getFailedTicketingOrders() {
        List<Order> orders = orderRepository.findFailedTicketingOrders();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 计算指定时间段内的收入
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总收入
     */
    public BigDecimal calculateRevenue(LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal revenue = orderRepository.calculateRevenueByDateRange(startTime, endTime);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    /**
     * 查找最近的订单（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 最近订单分页结果
     */
    public Page<OrderDTO> getRecentOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findRecentOrders(pageable);
        return orderPage.map(this::convertToDTO);
    }
    
    /**
     * 检查座位是否可用
     * @param flightId 航班ID
     * @param seatNumber 座位号
     * @return true表示可用，false表示已被预订
     */
    public boolean isSeatAvailable(Long flightId, String seatNumber) {
        Optional<Order> existingOrder = orderRepository.findByFlightIdAndSeatNumber(flightId, seatNumber);
        return !existingOrder.isPresent();
    }
    
    /**
     * 批量取消过期的待支付订单
     * @param expiredTime 过期时间
     * @return 取消的订单数量
     */
    public int cancelExpiredOrders(LocalDateTime expiredTime) {
        List<Order> expiredOrders = orderRepository.findExpiredPendingPaymentOrders(expiredTime);
        int cancelledCount = 0;
        
        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            cancelledCount++;
        }
        
        return cancelledCount;
    }
    
    /**
     * 重试出票失败的订单
     * @return 重试的订单DTO列表
     */
    public List<OrderDTO> retryFailedTicketingOrders() {
        List<Order> failedOrders = orderRepository.findFailedTicketingOrders();
        
        for (Order order : failedOrders) {
            // 这里可以添加重试出票的逻辑
            order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
            orderRepository.save(order);
        }
        
        return failedOrders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + String.format("%03d", new Random().nextInt(1000));
    }
}