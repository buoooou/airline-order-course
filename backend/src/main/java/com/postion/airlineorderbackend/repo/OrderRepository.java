package com.postion.airlineorderbackend.repo;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 查找特定状态且创建时间早于指定时间的订单
     * Find orders with specific status and created before given date
     * 
     * @param status 订单状态 (order status)
     * @param creationDate 截止创建时间 (cutoff creation date)
     * @return 符合条件的订单列表 (list of matching orders)
     */
    List<Order> findByStatusAndCreationDateBefore(OrderStatus status, LocalDateTime creationDate);
}