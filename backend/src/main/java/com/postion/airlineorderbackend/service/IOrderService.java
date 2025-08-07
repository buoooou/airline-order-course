package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderCreateRequest;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.dto.OrderUpdateRequest;
import com.postion.airlineorderbackend.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单服务接口
 * 定义订单相关的业务逻辑
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
public interface IOrderService {
    
    /**
     * 根据ID查找订单
     * 
     * @param id 订单ID
     * @return 订单DTO，如果不存在则返回空
     */
    Optional<OrderDTO> findById(Long id);
    
    /**
     * 根据订单号查找订单
     * 
     * @param orderNumber 订单号
     * @return 订单DTO，如果不存在则返回空
     */
    Optional<OrderDTO> findByOrderNumber(String orderNumber);
    
    /**
     * 根据用户ID查找订单列表
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页的订单DTO
     */
    Page<OrderDTO> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据订单状态查找订单
     * 
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 分页的订单DTO
     */
    Page<OrderDTO> findByStatus(OrderStatus status, Pageable pageable);
    
    /**
     * 创建新订单
     * 
     * @param createRequest 创建订单请求
     * @return 创建的订单DTO
     * @throws IllegalArgumentException 如果参数无效
     */
    OrderDTO createOrder(OrderCreateRequest createRequest);
    
    /**
     * 更新订单状态
     * 
     * @param orderId 订单ID
     * @param updateRequest 更新请求
     * @return 更新后的订单DTO
     * @throws IllegalArgumentException 如果订单不存在或状态转换无效
     */
    OrderDTO updateOrderStatus(Long orderId, OrderUpdateRequest updateRequest);
    
    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @param reason 取消原因
     * @return 取消后的订单DTO
     * @throws IllegalArgumentException 如果订单不存在或不允许取消
     */
    OrderDTO cancelOrder(Long orderId, String reason);
    
    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     * @return 支付后的订单DTO
     * @throws IllegalArgumentException 如果订单不存在或不允许支付
     */
    OrderDTO payOrder(Long orderId);
    
    /**
     * 获取用户的活跃订单
     * 
     * @param userId 用户ID
     * @return 活跃订单DTO列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<OrderDTO> getActiveOrdersByUser(Long userId);
    
    /**
     * 查找超时未支付的订单
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单DTO列表
     */
    List<OrderDTO> findTimeoutOrders(int timeoutMinutes);
    
    /**
     * 自动取消超时订单
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 取消的订单数量
     */
    int cancelTimeoutOrders(int timeoutMinutes);
    
    /**
     * 多条件搜索订单
     * 
     * @param orderNumber 订单号（可为空）
     * @param userId 用户ID（可为空）
     * @param status 订单状态（可为空）
     * @param startTime 开始时间（可为空）
     * @param endTime 结束时间（可为空）
     * @param pageable 分页参数
     * @return 分页的订单DTO
     */
    Page<OrderDTO> findByMultipleConditions(String orderNumber, Long userId, OrderStatus status,
                                           LocalDateTime startTime, LocalDateTime endTime,
                                           Pageable pageable);
    
    /**
     * 获取订单统计信息
     * 
     * @return 订单统计结果
     */
    List<Object[]> getOrderStatistics();
}
