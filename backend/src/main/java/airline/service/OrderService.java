package airline.service;

import com.airline.entity.Order;
import com.airline.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {
    Page<Order> list(Pageable pageable);

    Order findDetail(Long id);

    Order updateStatus(Long id, OrderStatus status);
}
