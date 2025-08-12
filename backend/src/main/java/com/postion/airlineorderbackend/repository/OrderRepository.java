package com.postion.airlineorderbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatusAndCreationDateBefore(OrderStatus status, LocalDateTime before);
}
