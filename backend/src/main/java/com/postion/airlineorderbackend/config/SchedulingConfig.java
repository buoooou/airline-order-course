package com.postion.airlineorderbackend.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.sql.DataSource;

/**
 * 定时任务配置：适配ShedLock 5.14.0版本的分布式锁配置
 */
@Configuration
@EnableScheduling // 开启定时任务
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S") // 默认锁最大持有30秒
public class SchedulingConfig {

    /**
     * 配置基于MySQL的分布式锁存储
     * 注意：ShedLock 5.x 版本中 JdbcTemplateLockProvider 的构建方式已变更
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        // 1. 创建JdbcTemplate（依赖数据源）
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // 2. 构建锁提供者（适配5.14.0版本的构造方式）
        // 无需指定LockTableSchema，默认会适配MySQL的表结构
        return new JdbcTemplateLockProvider(jdbcTemplate);
    }
}