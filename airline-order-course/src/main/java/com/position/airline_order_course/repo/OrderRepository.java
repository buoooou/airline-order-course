package com.position.airline_order_course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.position.airline_order_course.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 查询所有订单，使用JpaRepository接口默认的findAll()
    // List<Order> findAll();

    // 根据Id查询单个订单
    Order getOrderById(Long id);

}
