package com.postion.airlineorderbackend.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * ShedLock分布式锁配置类
 * 用于确保在分布式环境中定时任务只在一个实例上执行
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m") // 默认最长锁定时间10分钟
public class ShedLockConfig {
    
    private final DataSource dataSource;
    
    public ShedLockConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * 初始化ShedLock表
     * 在应用启动时自动创建shedlock表（如果不存在）
     */
    @PostConstruct
    public void initShedLockTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        try {
            // 检查表是否存在
            jdbcTemplate.execute("SELECT 1 FROM shedlock LIMIT 1");
            System.out.println("ShedLock表已存在，跳过创建");
        } catch (Exception e) {
            // 表不存在，创建表
            System.out.println("正在创建ShedLock表...");
            String createTableSql = "CREATE TABLE `shedlock` (" +
                "`name` varchar(64) NOT NULL COMMENT '锁名称'," +
                "`lock_until` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '锁定到期时间'," +
                "`locked_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '锁定时间'," +
                "`locked_by` varchar(255) NOT NULL COMMENT '锁定者标识'," +
                "PRIMARY KEY (`name`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ShedLock分布式锁表'";
            
            try {
                jdbcTemplate.execute(createTableSql);
                System.out.println("ShedLock表创建成功！");
            } catch (Exception createException) {
                System.err.println("创建ShedLock表失败: " + createException.getMessage());
                // 不抛出异常，让应用继续启动
            }
        }
    }
    
    /**
     * 配置ShedLock的锁提供者
     * 使用JDBC模板作为锁的存储后端
     * 
     * @param dataSource 数据源
     * @return LockProvider实例
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName("shedlock") // 锁表名称
                .build()
        );
    }
}
