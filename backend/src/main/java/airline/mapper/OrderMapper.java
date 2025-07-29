package airline.mapper;

import com.airline.dto.FlightInfoDto;
import com.airline.dto.OrderDetailDto;
import com.airline.dto.OrderListDto;
import com.airline.entity.FlightInfo;
import com.airline.entity.Order;
import com.airline.repository.FlightInfoRepository;
import com.airline.repository.OrderRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class OrderMapper {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FlightInfoRepository flightInfoRepository;

    public  OrderListDto toListDto(Order order) {
        return new OrderListDto(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }

    public  OrderDetailDto toDetailDto(Order order) {
        FlightInfo flight = flightInfoRepository.findByOrderId(order.getId());
        FlightInfoDto flightDto = new FlightInfoDto(
                flight.getFlightNumber(),
                flight.getDepartureCity(),
                flight.getArrivalCity(),
                flight.getDepartureTime()
        );
        return new OrderDetailDto(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getCreatedAt(),
                flightDto
        );
    }


}