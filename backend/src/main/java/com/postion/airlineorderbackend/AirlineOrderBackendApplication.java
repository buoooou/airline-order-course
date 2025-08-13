package com.postion.airlineorderbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableAsync // Enable async support
@EnableScheduling // Enable scheduled tasks
public class AirlineOrderBackendApplication {

    public static void main(String[] args) {
        log.info("Starting AirlineOrderBackendApplication...");
        SpringApplication.run(AirlineOrderBackendApplication.class, args);
    }

}
