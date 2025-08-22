package com.postion.airlineorderbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // Enable Spring Boot Auto Configuration
@EnableAsync // 开启异步方法支持（与AsyncConfig配合使用）
@EnableScheduling // 开启定时任务支持（与SchedulingConfig配合使用）
public class AirlineOrderBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirlineOrderBackendApplication.class, args);
    }

}
