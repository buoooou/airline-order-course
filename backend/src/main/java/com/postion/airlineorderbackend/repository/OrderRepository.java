package com.postion.airlineorderbackend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE "
            + "o.status = :status AND o.createTime < :createTime ORDER BY o.createTime DESC")
    List<Order> findByStatusAndCreateTimeBefore(@Param("status") OrderStatus status, @Param("createTime") LocalDateTime createTime);
}
