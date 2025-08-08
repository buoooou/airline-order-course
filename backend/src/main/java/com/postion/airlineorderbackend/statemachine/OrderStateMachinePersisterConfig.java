package com.postion.airlineorderbackend.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

/**
 * 订单状态机JPA持久化配置
 * 使用JPA数据库存储状态机上下文，支持跨会话的状态持久化
 * 统一使用JPA持久化，确保状态数据持久化存储
 */
@Configuration
public class OrderStateMachinePersisterConfig {

    @Bean
    @Primary
    public StateMachineRuntimePersister<OrderState, OrderEvent, String> stateMachineRuntimePersister(
            JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Bean
    public StateMachinePersister<OrderState, OrderEvent, String> stateMachinePersister(
            StateMachineRuntimePersister<OrderState, OrderEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachinePersister<>(stateMachineRuntimePersister);
    }
}