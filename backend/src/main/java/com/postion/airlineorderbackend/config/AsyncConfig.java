package com.postion.airlineorderbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

/**
 * 异步任务配置类：定义线程池参数，开启异步支持
 */
@Configuration
@EnableAsync // 开启异步方法支持
public class AsyncConfig {

    /**
     * 自定义异步线程池
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：默认活跃线程数
        executor.setCorePoolSize(5);
        // 最大线程数：线程池最大可创建线程数
        executor.setMaxPoolSize(10);
        // 队列容量：任务等待队列大小
        executor.setQueueCapacity(50);
        // 线程名前缀：便于日志排查
        executor.setThreadNamePrefix("Async-Order-");
        // 线程空闲时间：超过核心线程数的线程空闲多久后销毁
        executor.setKeepAliveSeconds(60);
        // 拒绝策略：当任务满时，直接在提交线程中执行任务
        executor.setRejectedExecutionHandler((r, executor1) -> {
            if (!executor1.isShutdown()) {
                r.run();
            }
        });
        executor.initialize();
        return executor;
    }
}