package com.position.airline_order_course.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.position.airline_order_course.dto.OrderStatus;
import com.position.airline_order_course.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根据Id查询单个订单
    Order getOrderById(Long id);

    // 查找所有状态为PENDING且创建时间早于指定时间的订单
    List<Order> findByStatusAndCreationDate(OrderStatus status, LocalDateTime creationDate);

}
