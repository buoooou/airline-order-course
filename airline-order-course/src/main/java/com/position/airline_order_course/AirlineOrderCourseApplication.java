package com.position.airline_order_course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.position.airline_order_course.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class) // 读取配置文件
public class AirlineOrderCourseApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirlineOrderCourseApplication.class, args);
	}

}
