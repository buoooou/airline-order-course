package com.postion.airlineorderbackend.repository;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' AND o.creationDate < :cutoffTime")
    List<Order> findPendingPaymentOrdersBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT o FROM Order o WHERE o.status = 'PAID' AND o.creationDate < :cutoffTime")
    List<Order> findPaidOrdersBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
} 