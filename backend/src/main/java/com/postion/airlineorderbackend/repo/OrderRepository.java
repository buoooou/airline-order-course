package com.postion.airlineorderbackend.repo;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 根据用户 ID 查询订单
    List<Order> findByUserId(Long userId);

    // 根据订单状态查询订单
    List<Order> findByStatus(OrderStatus status);
}