package com.postion.airlineorderbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirlineOrderBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirlineOrderBackendApplication.class, args);
    }

}
