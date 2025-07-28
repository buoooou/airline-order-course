package com.airline.order.repository;

import com.airline.order.entity.Order;
import com.airline.order.entity.Order.OrderStatus;
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
 * 订单数据访问层接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据订单号查找订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 根据用户ID查找订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据用户ID查找订单列表（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据航班ID查找订单列表
     * @param flightId 航班ID
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.flightInfo.id = :flightId")
    List<Order> findByFlightId(@Param("flightId") Long flightId);
    
    /**
     * 根据订单状态查找订单列表
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * 根据订单状态查找订单列表（分页）
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    /**
     * 根据用户ID和状态查找订单
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    /**
     * 根据用户ID和状态查找订单（分页）
     * @param userId 用户ID
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status, Pageable pageable);
    
    /**
     * 根据创建时间范围查找订单
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.creationDate BETWEEN :startTime AND :endTime")
    List<Order> findByCreationDateBetween(
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据金额范围查找订单
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.amount BETWEEN :minAmount AND :maxAmount")
    List<Order> findByAmountBetween(
            @Param("minAmount") BigDecimal minAmount, 
            @Param("maxAmount") BigDecimal maxAmount);
    
    /**
     * 根据座位号查找订单
     * @param seatNumber 座位号
     * @return 订单列表
     */
    List<Order> findBySeatNumber(String seatNumber);
    
    /**
     * 查找指定航班的指定座位是否已被预订
     * @param flightId 航班ID
     * @param seatNumber 座位号
     * @return 订单对象
     */
    @Query("SELECT o FROM Order o WHERE o.flightInfo.id = :flightId AND o.seatNumber = :seatNumber AND o.status NOT IN ('CANCELLED')")
    Optional<Order> findByFlightIdAndSeatNumber(
            @Param("flightId") Long flightId, 
            @Param("seatNumber") String seatNumber);
    
    /**
     * 统计指定状态的订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);
    
    /**
     * 统计指定用户的订单数量
     * @param userId 用户ID
     * @return 订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 查找需要处理的待支付订单（超过指定时间未支付）
     * @param expiredTime 过期时间
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' AND o.creationDate < :expiredTime")
    List<Order> findExpiredPendingPaymentOrders(@Param("expiredTime") LocalDateTime expiredTime);
    
    /**
     * 查找需要重试出票的订单
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'TICKETING_FAILED'")
    List<Order> findFailedTicketingOrders();
    
    /**
     * 根据用户名查找订单
     * @param username 用户名
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.username = :username")
    List<Order> findByUsername(@Param("username") String username);
    
    /**
     * 根据航班号查找订单
     * @param flightNumber 航班号
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o JOIN o.flightInfo f WHERE f.flightNumber = :flightNumber")
    List<Order> findByFlightNumber(@Param("flightNumber") String flightNumber);
    
    /**
     * 查找指定时间段内的收入统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总收入
     */
    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.status IN ('PAID', 'TICKETING_IN_PROGRESS', 'TICKETED') AND o.creationDate BETWEEN :startTime AND :endTime")
    BigDecimal calculateRevenueByDateRange(
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找最近的订单（分页）
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o ORDER BY o.creationDate DESC")
    Page<Order> findRecentOrders(Pageable pageable);
}