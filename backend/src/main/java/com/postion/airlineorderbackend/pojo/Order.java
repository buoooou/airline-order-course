package main.java.com.postion.airlineorderbackend.pojo;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, unique = true)
    private String ordernumber;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationdate = LocalDateTime.now();

    @Column(nullable = false, unique = true)
    private String userid;

}
