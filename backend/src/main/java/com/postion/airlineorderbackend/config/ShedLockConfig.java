package com.postion.airlineorderbackend.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M") // 默认锁定最多30分钟
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .withTableName("shedlock")
                        .withColumnNames(
                                JdbcTemplateLockProvider.ColumnNames.builder()
                                        .lockName("name")
                                        .lockUntil("lock_until")
                                        .lockedAt("locked_at")
                                        .lockedBy("locked_by")
                                        .build()
                        )
                        .build()
        );
    }
}