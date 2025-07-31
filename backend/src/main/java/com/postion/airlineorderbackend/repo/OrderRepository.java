package com.postion.airlineorderbackend.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByStatusAndCreationDateBefore(OrderStatus status, LocalDateTime creationDate);

    // 查询所有订单
    List<Order> findAll();
}
