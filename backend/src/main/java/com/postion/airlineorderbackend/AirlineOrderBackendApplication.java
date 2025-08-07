package com.postion.airlineorderbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
// import org.springframework.scheduling.annotation.EnableAsync;
// import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.postion.airlineorderbackend.entity")
public class AirlineOrderBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirlineOrderBackendApplication.class, args);
    }

}
