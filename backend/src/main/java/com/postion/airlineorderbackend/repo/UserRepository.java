package com.postion.airlineorderbackend.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;

public interface UserRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreateionDateBefore(OrderStatus status, LocalDateTime creationDate);
}
