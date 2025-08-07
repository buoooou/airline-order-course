package com.postion.airlineorderbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库配置类
 * 支持远程数据库连接失败时自动切换到本地数据库
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.primary.url}")
    private String primaryUrl;

    @Value("${spring.datasource.primary.username}")
    private String primaryUsername;

    @Value("${spring.datasource.primary.password}")
    private String primaryPassword;

    @Value("${spring.datasource.fallback.url}")
    private String fallbackUrl;

    @Value("${spring.datasource.fallback.username}")
    private String fallbackUsername;

    @Value("${spring.datasource.fallback.password}")
    private String fallbackPassword;

    /**
     * 创建主数据源，支持自动切换
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        // 首先尝试连接远程数据库
        DataSource primaryDataSource = createDataSource(primaryUrl, primaryUsername, primaryPassword);
        
        if (testConnection(primaryDataSource)) {
            System.out.println("✅ 成功连接到远程数据库: " + primaryUrl);
            return primaryDataSource;
        } else {
            System.out.println("❌ 远程数据库连接失败，切换到本地数据库");
            DataSource fallbackDataSource = createDataSource(fallbackUrl, fallbackUsername, fallbackPassword);
            
            if (testConnection(fallbackDataSource)) {
                System.out.println("✅ 成功连接到本地数据库: " + fallbackUrl);
                return fallbackDataSource;
            } else {
                System.err.println("❌ 本地数据库连接也失败，请检查数据库配置");
                throw new RuntimeException("无法连接到任何数据库，请检查数据库配置和服务状态");
            }
        }
    }

    /**
     * 创建数据源
     */
    private DataSource createDataSource(String url, String username, String password) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

    /**
     * 测试数据库连接
     */
    private boolean testConnection(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5秒超时
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 创建JdbcTemplate Bean
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
