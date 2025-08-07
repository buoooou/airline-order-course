package com.postion.airlineorderbackend.repository;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 * 提供订单相关的数据库操作方法
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据订单号查找订单
     * 用于订单号唯一性检查和查询
     * 
     * @param orderNumber 订单号
     * @return 订单信息（可能为空）
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 检查订单号是否已存在
     * 用于订单号重复检查
     * 
     * @param orderNumber 订单号
     * @return 是否存在
     */
    boolean existsByOrderNumber(String orderNumber);
    
    /**
     * 根据用户查找订单列表
     * 用于用户查看自己的订单
     * 
     * @param user 用户对象
     * @param pageable 分页参数
     * @return 分页的订单信息
     */
    Page<Order> findByUser(User user, Pageable pageable);
    
    /**
     * 根据用户ID查找订单列表
     * 用于用户查看自己的订单
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据订单状态查找订单
     * 用于按状态筛选订单
     * 
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 分页的订单信息
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    /**
     * 根据用户和订单状态查找订单
     * 用于用户查看特定状态的订单
     * 
     * @param user 用户对象
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByUserAndStatus(User user, OrderStatus status);
    
    /**
     * 根据创建时间范围查找订单
     * 用于按时间段筛选订单
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.creationDate BETWEEN :startTime AND :endTime")
    List<Order> findByCreationDateBetween(@Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找超时未支付的订单
     * 用于自动取消超时订单
     * 
     * @param timeoutTime 超时时间点
     * @return 超时订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' " +
           "AND o.creationDate < :timeoutTime")
    List<Order> findTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 根据金额范围查找订单
     * 用于金额筛选功能
     * 
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.amount BETWEEN :minAmount AND :maxAmount")
    List<Order> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, 
                                   @Param("maxAmount") BigDecimal maxAmount);
    
    /**
     * 统计指定状态的订单数量
     * 用于统计分析
     * 
     * @param status 订单状态
     * @return 订单数量
     */
    long countByStatus(OrderStatus status);
    
    /**
     * 统计指定用户的订单数量
     * 用于用户数据分析
     * 
     * @param user 用户对象
     * @return 订单数量
     */
    long countByUser(User user);
    
    /**
     * 查找指定用户的最近订单
     * 用于显示用户最近的订单记录
     * 
     * @param user 用户对象
     * @param limit 返回数量限制
     * @return 最近的订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.creationDate DESC")
    List<Order> findRecentOrdersByUser(@Param("user") User user, Pageable pageable);
    
    /**
     * 查找今日订单
     * 用于日报统计
     * 
     * @param startOfDay 今日开始时间
     * @param endOfDay 今日结束时间
     * @return 今日订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.creationDate BETWEEN :startOfDay AND :endOfDay")
    List<Order> findTodayOrders(@Param("startOfDay") LocalDateTime startOfDay, 
                               @Param("endOfDay") LocalDateTime endOfDay);
    
    /**
     * 计算指定时间范围内的订单总金额
     * 用于收入统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 订单状态（只统计已完成的订单）
     * @return 订单总金额
     */
    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.creationDate BETWEEN :startTime AND :endTime " +
           "AND o.status = :status")
    BigDecimal sumAmountByDateRangeAndStatus(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("status") OrderStatus status);
    
    /**
     * 查找需要出票的订单（已支付状态）
     * 用于出票任务调度
     * 
     * @return 需要出票的订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PAID' ORDER BY o.paymentTime ASC")
    List<Order> findOrdersNeedingTicketing();
    
    /**
     * 查找出票失败的订单
     * 用于重试出票或人工处理
     * 
     * @return 出票失败的订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'TICKETING_FAILED' ORDER BY o.lastUpdated DESC")
    List<Order> findFailedTicketingOrders();
    
    /**
     * 根据航班信息ID查找订单
     * 用于航班相关的订单查询
     * 
     * @param flightInfoId 航班信息ID
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.flightInfo.id = :flightInfoId")
    List<Order> findByFlightInfoId(@Param("flightInfoId") Long flightInfoId);
    
    /**
     * 查找指定航班的有效订单（未取消）
     * 用于航班座位管理
     * 
     * @param flightInfoId 航班信息ID
     * @return 有效订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.flightInfo.id = :flightInfoId " +
           "AND o.status != 'CANCELLED'")
    List<Order> findValidOrdersByFlightInfoId(@Param("flightInfoId") Long flightInfoId);
    
    /**
     * 多条件搜索订单
     * 用于管理员的高级搜索功能
     * 
     * @param orderNumber 订单号（可为空）
     * @param userId 用户ID（可为空）
     * @param status 订单状态（可为空）
     * @param startTime 开始时间（可为空）
     * @param endTime 结束时间（可为空）
     * @param pageable 分页参数
     * @return 分页的订单信息
     */
    @Query("SELECT o FROM Order o WHERE " +
           "(:orderNumber IS NULL OR o.orderNumber LIKE %:orderNumber%) AND " +
           "(:userId IS NULL OR o.user.id = :userId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startTime IS NULL OR o.creationDate >= :startTime) AND " +
           "(:endTime IS NULL OR o.creationDate <= :endTime) " +
           "ORDER BY o.creationDate DESC")
    Page<Order> findByMultipleConditions(@Param("orderNumber") String orderNumber,
                                        @Param("userId") Long userId,
                                        @Param("status") OrderStatus status,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime,
                                        Pageable pageable);
    
    /**
     * 查找用户的活跃订单（非终态）
     * 用于用户订单管理
     * 
     * @param user 用户对象
     * @return 活跃订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.user = :user " +
           "AND o.status NOT IN ('TICKETED', 'CANCELLED') " +
           "ORDER BY o.creationDate DESC")
    List<Order> findActiveOrdersByUser(@Param("user") User user);
    
    /**
     * 统计各状态订单的数量
     * 用于仪表板统计
     * 
     * @return 状态统计结果
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();
    
    /**
     * 查找最近创建的订单
     * 用于实时监控
     * 
     * @param limit 返回数量限制
     * @return 最近的订单列表
     */
    @Query("SELECT o FROM Order o ORDER BY o.creationDate DESC")
    List<Order> findRecentOrders(Pageable pageable);
}
