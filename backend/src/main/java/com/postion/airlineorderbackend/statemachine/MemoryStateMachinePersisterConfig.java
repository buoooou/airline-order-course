package com.postion.airlineorderbackend.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单状态机内存持久化配置
 * 使用内存ConcurrentHashMap存储状态机上下文，性能高但会话结束后数据丢失
 * 适用于测试环境和开发环境，提高测试速度和开发效率
 * 生产环境请使用JPA持久化配置
 */
@Configuration
@Profile({"test", "dev"})
public class MemoryStateMachinePersisterConfig {

    @Bean
    @Primary
    public StateMachinePersister<OrderState, OrderEvent, String> memoryStateMachinePersister() {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<OrderState, OrderEvent, String>() {
            private final ConcurrentHashMap<String, StateMachineContext<OrderState, OrderEvent>> contexts = new ConcurrentHashMap<>();

            @Override
            public void write(StateMachineContext<OrderState, OrderEvent> context, String contextObj) {
                if (contextObj == null || contextObj.trim().isEmpty()) {
                    throw new IllegalArgumentException("状态机ID不能为空");
                }
                contexts.put(contextObj, context);
            }

            @Override
            public StateMachineContext<OrderState, OrderEvent> read(String contextObj) {
                return contexts.get(contextObj);
            }
        });
    }
}