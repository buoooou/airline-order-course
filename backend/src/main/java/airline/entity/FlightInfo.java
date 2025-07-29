package airline.entity;

import javax.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_flight_info")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class FlightInfo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long orderId;

    @Column(nullable = false, length = 10)
    private String flightNumber;

    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureTime;
}