package airline.mapper;

import airline.dto.FlightInfoDto;
import airline.dto.OrderDetailDto;
import airline.dto.OrderListDto;
import airline.entity.FlightInfo;
import airline.entity.Order;
import airline.entity.User;
import airline.repository.FlightInfoRepository;
import airline.repository.OrderRepository;
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
       /* FlightInfo flight = flightInfoRepository.findByOrderId(order.getId());
        FlightInfoDto flightDto = new FlightInfoDto(
                flight.getFlightNumber(),
                flight.getDepartureCity(),
                flight.getArrivalCity(),
                flight.getDepartureTime()
        );*/
        OrderDetailDto orderDetailDto = new OrderDetailDto(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getCreatedAt(),
                new FlightInfoDto(),
                new User(),
                order.getTotalAmount()

        );
        User user = orderDetailDto.getUser();
        user.setId(1L);
        user.setUsername("admin");
        return orderDetailDto;
    }


}