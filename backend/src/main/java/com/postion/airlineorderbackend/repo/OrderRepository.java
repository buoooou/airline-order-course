package com.postion.airlineorderbackend.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 新增方法，用于查找特定状态和创建时间早于某个时间的订单
    List<Order> findByStatusAndCreationDateBefore(OrderStatus status, LocalDateTime creationDate);
}