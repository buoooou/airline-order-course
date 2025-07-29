package airline.repository;

import com.airline.entity.FlightInfo;
import com.airline.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FlightInfoRepository extends JpaRepository<FlightInfo, Long> {

    @Query(value = "select * from  t_flight_info where order_id = :orderId limit 1", nativeQuery = true)
    FlightInfo findByOrderId(Long orderId);

}
