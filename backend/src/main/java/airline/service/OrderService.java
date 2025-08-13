package airline.service;

import airline.entity.Order;
import airline.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {
    Page<Order> list(Pageable pageable);

    Order findDetail(Long id);

    Order updateStatus(Long id, OrderStatus status);
}
