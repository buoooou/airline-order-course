package com.postion.airlineorderbackend.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 应用配置类
 * 用于配置组件扫描和JPA仓库
 */
@Configuration
@ComponentScan(basePackages = {"com.airline.order"})
@EnableJpaRepositories(basePackages = {"com.airline.order.repository"})
public class AppConfig {
    // 配置类可以为空，注解已经完成了所需的配置
}