package com.position.airlineorderbackend.repo;

import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 查找超时的待支付订单（超过指定时间未支付）
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdDate < :timeoutTime")
    List<Order> findTimeoutOrders(@Param("status") OrderStatus status, @Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 查找出票失败的订单
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findOrdersByStatus(@Param("status") OrderStatus status);
    
    /**
     * 查找指定时间范围内创建的订单
     */
    @Query("SELECT o FROM Order o WHERE o.createdDate BETWEEN :startTime AND :endTime")
    List<Order> findOrdersByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计各状态订单数量
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();
} 