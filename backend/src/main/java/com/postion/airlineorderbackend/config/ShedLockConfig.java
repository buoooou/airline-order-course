package com.postion.airlineorderbackend.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * ShedLock分布式锁配置
 * 
 * ShedLock确保定时任务在分布式环境中只在一个节点上执行
 * 默认使用数据库存储锁信息
 */
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10m") // 默认锁最长持有10分钟
public class ShedLockConfig {

    /**
     * 配置LockProvider
     * 使用JDBC模板作为锁存储
     * 
     * @param dataSource 数据源
     * @return LockProvider实例
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime() // 使用数据库时间
                        .build()
        );
    }
}