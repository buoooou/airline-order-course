package com.airline.repository;

import com.airline.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    Page<Order> findByStatus(@Param("status") Order.Status status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :paymentStatus")
    Page<Order> findByPaymentStatus(@Param("paymentStatus") Order.PaymentStatus paymentStatus, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.bookingDate BETWEEN :startDate AND :endDate")
    List<Order> findByBookingDateBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE " +
           "(LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.contactName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.contactEmail) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") Order.Status status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = :paymentStatus")
    long countByPaymentStatus(@Param("paymentStatus") Order.PaymentStatus paymentStatus);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.bookingDate < :cutoffTime")
    List<Order> findExpiredPendingOrders(@Param("cutoffTime") LocalDateTime cutoffTime);
}