package com.postion.airlineorderbackend.config;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;

@Configuration
// 启用 ShedLock，默认锁前缀为 "shedlock:"
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S") 
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        // 使用 Redis 连接工厂创建锁提供者
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
               .withJdbcTemplate(new JdbcTemplate(dataSource))
               .usingDbTime()
               .build());
    }
}
    