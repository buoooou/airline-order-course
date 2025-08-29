package com.postion.airlineorderbackend.scheduler;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderCleanupScheduler {

    @Autowired
    private OrderRepository orderRepository;

    // 每分钟扫描一次，取消创建超过30分钟仍未支付的订单
    @Scheduled(fixedRate = 60000)
    public void cancelExpiredPendingOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<Order> orders = orderRepository.findAll();
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.PENDING_PAYMENT && o.getCreationDate() != null && o.getCreationDate().isBefore(threshold)) {
                o.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(o);
            }
        }
    }
}
