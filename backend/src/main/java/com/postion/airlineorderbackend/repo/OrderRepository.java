package com.postion.airlineorderbackend.repo;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 根据状态和创建时间查找订单
    List<Order> findByStatusAndCreationDateBefore(OrderStatus status, LocalDateTime creationDate);
}
