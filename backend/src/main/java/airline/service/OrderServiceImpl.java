package airline.service;

import com.airline.entity.Order;
import com.airline.enums.OrderStatus;
import com.airline.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private  OrderRepository orderRepository;
    @Override
    public Page<Order> list(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order findDetail(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);

        return optionalOrder.orElseGet(Order::new);
    }



    @Override
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new RuntimeException(
                    "Cannot transition from " + order.getStatus() + " to " + newStatus);
        }
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
