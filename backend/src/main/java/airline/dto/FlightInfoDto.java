package airline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FlightInfoDto {
    String flightNumber;
    String departureCity;
    String arrivalCity;
    LocalDateTime departureTime;
}
