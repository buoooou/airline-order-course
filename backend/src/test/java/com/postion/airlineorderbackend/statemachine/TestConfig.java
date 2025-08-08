package com.postion.airlineorderbackend.statemachine;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * 测试配置类 - 用于状态机测试
 * 根据测试需求选择合适的持久化配置
 */
@TestConfiguration
@ComponentScan(basePackages = "com.postion.airlineorderbackend.statemachine")
@Import({
    OrderStateMachineConfig.class,
    MemoryStateMachinePersisterConfig.class  // 测试环境使用内存持久化，提高测试速度
})
@Profile("test")
public class TestConfig {
    // 使用内存持久化，无需数据库依赖
}