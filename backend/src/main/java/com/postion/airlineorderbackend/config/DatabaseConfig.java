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
 * 支持H2、MySQL等多种数据库，优先使用环境变量配置
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:}")
    private String datasourcePassword;

    @Value("${spring.datasource.driver-class-name:}")
    private String driverClassName;

    @Value("${spring.datasource.primary.url:}")
    private String primaryUrl;

    @Value("${spring.datasource.primary.username:}")
    private String primaryUsername;

    @Value("${spring.datasource.primary.password:}")
    private String primaryPassword;

    @Value("${spring.datasource.fallback.url:}")
    private String fallbackUrl;

    @Value("${spring.datasource.fallback.username:}")
    private String fallbackUsername;

    @Value("${spring.datasource.fallback.password:}")
    private String fallbackPassword;

    /**
     * 创建主数据源，支持自动切换
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        // 如果环境变量中直接配置了数据源，优先使用
        if (!datasourceUrl.isEmpty()) {
            System.out.println("✅ 使用环境变量配置的数据源: " + datasourceUrl);
            return DataSourceBuilder.create()
                    .url(datasourceUrl)
                    .username(datasourceUsername)
                    .password(datasourcePassword)
                    .driverClassName(driverClassName.isEmpty() ? getDriverByUrl(datasourceUrl) : driverClassName)
                    .build();
        }

        // 否则使用原有的主备切换逻辑
        if (!primaryUrl.isEmpty()) {
            DataSource primaryDataSource = createDataSource(primaryUrl, primaryUsername, primaryPassword);
            
            if (testConnection(primaryDataSource)) {
                System.out.println("✅ 成功连接到远程数据库: " + primaryUrl);
                return primaryDataSource;
            } else {
                System.out.println("❌ 远程数据库连接失败，切换到本地数据库");
                if (!fallbackUrl.isEmpty()) {
                    DataSource fallbackDataSource = createDataSource(fallbackUrl, fallbackUsername, fallbackPassword);
                    
                    if (testConnection(fallbackDataSource)) {
                        System.out.println("✅ 成功连接到本地数据库: " + fallbackUrl);
                        return fallbackDataSource;
                    }
                }
            }
        }

        // 最后的备选方案：H2内存数据库
        System.out.println("✅ 使用H2内存数据库作为备选方案");
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:airline_order_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
    }

    /**
     * 根据URL推断驱动类名
     */
    private String getDriverByUrl(String url) {
        if (url.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        } else if (url.startsWith("jdbc:mysql:")) {
            return "com.mysql.cj.jdbc.Driver";
        }
        return "org.h2.Driver"; // 默认使用H2
    }

    /**
     * 创建数据源
     */
    private DataSource createDataSource(String url, String username, String password) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(getDriverByUrl(url))
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
