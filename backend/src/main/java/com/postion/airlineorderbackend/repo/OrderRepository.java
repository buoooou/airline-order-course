package com.postion.airlineorderbackend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 查询所有订单
    List<Order> findAll();
    // 根据用户id查询订单
    List<Order> findAllByUserId(Long userId);

    // 新增方法，查找特定状态和创建时间早于某个时间的订单
    List<Order> findByStatusAndCreationDateBefore(OrderStatus status, LocalDateTime creationDate);
}
