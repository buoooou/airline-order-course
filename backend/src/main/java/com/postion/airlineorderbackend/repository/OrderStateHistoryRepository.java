package com.postion.airlineorderbackend.repository;

import com.postion.airlineorderbackend.entity.OrderStateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStateHistoryRepository extends JpaRepository<OrderStateHistory, Long> {

    List<OrderStateHistory> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    List<OrderStateHistory> findByOrderIdAndSuccessOrderByCreatedAtDesc(Long orderId, Boolean success);

    List<OrderStateHistory> findBySuccessOrderByCreatedAtDesc(Boolean success);

    @Query("SELECT h FROM OrderStateHistory h WHERE h.orderId = :orderId AND h.success = false ORDER BY h.createdAt DESC")
    List<OrderStateHistory> findFailedTransitionsByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT h FROM OrderStateHistory h WHERE h.orderNumber = :orderNumber ORDER BY h.createdAt DESC")
    List<OrderStateHistory> findByOrderNumberOrderByCreatedAtDesc(@Param("orderNumber") String orderNumber);
}