package com.postion.airlineorderbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

/**
 * 异步配置类
 * 
 * <p>
 * 启用Spring的异步执行功能，配置线程池和RestTemplate
 * </p>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 创建RestTemplate bean
     * 
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 创建异步任务执行器
     * 配置专业的线程池参数：
     * - 核心线程数：10
     * - 最大线程数：50
     * - 队列容量：100
     * - 线程名称前缀：Async-
     * - 拒绝策略：调用者运行策略
     * 
     * @return Executor实例
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}