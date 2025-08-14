package airline.dto;

import airline.entity.User;
import airline.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetailDto {
    private Long id;
    private String orderNumber;
   private OrderStatus status;
   private LocalDateTime creationDate;
   private FlightInfoDto flight;
   private User user;
    private BigDecimal amount;
}
