package com.postion.airlineorderbackend.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

/**
 * 状态机持久化配置
 */
@Configuration
public class OrderStateMachinePersisterConfig {

    @Bean
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