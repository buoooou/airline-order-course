package airline.dto;

import airline.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetailDto {
    private Long id;
    private String orderNumber;
   private OrderStatus status;
   private LocalDateTime createdAt;
   private FlightInfoDto flight;
}
