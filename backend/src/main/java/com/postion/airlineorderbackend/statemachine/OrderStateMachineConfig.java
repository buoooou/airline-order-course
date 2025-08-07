package com.postion.airlineorderbackend.statemachine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * 订单状态机配置
 * 定义状态机的状态、转换规则和行为
 */
@Configuration
@EnableStateMachineFactory(name = "orderStateMachineFactory")
@RequiredArgsConstructor
@Slf4j
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    private final OrderStateActionHandler actionHandler;

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        // 配置状态机的全局设置
        // 禁用自动启动，需要手动触发状态机启动，便于控制状态机的生命周期
        config
            .withConfiguration()
                .autoStartup(false);
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        // 配置状态机的状态定义
        // 设置初始状态为待支付(PENDING_PAYMENT)
        // 定义所有可能的状态（使用枚举中的所有状态）
        // 设置两个终止状态：已出票(TICKETED)和已取消(CANCELLED)
        states
            .withStates()
                .initial(OrderState.PENDING_PAYMENT)
                .states(EnumSet.allOf(OrderState.class))
                .end(OrderState.TICKETED)
                .end(OrderState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        // 配置状态机的状态转换规则
        // 定义所有可能的状态转换路径，包括源状态、目标状态和触发事件
        // 每个转换都包含守卫条件(guard)和动作处理(action)以确保转换的合法性和一致性
        transitions
            // 待支付 -> 已支付：用户完成支付操作
            .withExternal()
                .source(OrderState.PENDING_PAYMENT)
                .target(OrderState.PAID)
                .event(OrderEvent.PAY)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            
            // 待支付 -> 已取消：用户主动取消订单
            .withExternal()
                .source(OrderState.PENDING_PAYMENT)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            // 待支付 -> 已取消：系统自动超时取消
            .withExternal()
                .source(OrderState.PENDING_PAYMENT)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.AUTO_CANCEL)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            
            // 已支付 -> 出票中：系统开始处理出票
            .withExternal()
                .source(OrderState.PAID)
                .target(OrderState.TICKETING_IN_PROGRESS)
                .event(OrderEvent.PROCESS_TICKETING)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            
            // 已支付 -> 已取消：用户取消已支付的订单（需要退款处理）
            .withExternal()
                .source(OrderState.PAID)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            
            // 出票中 -> 已出票：出票流程成功完成
            .withExternal()
                .source(OrderState.TICKETING_IN_PROGRESS)
                .target(OrderState.TICKETED)
                .event(OrderEvent.TICKETING_SUCCESS)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            
            // 出票中 -> 出票失败：出票流程执行失败
            .withExternal()
                .source(OrderState.TICKETING_IN_PROGRESS)
                .target(OrderState.TICKETING_FAILED)
                .event(OrderEvent.TICKETING_FAILURE)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            
            // 出票中 -> 已取消：在出票过程中取消订单
            .withExternal()
                .source(OrderState.TICKETING_IN_PROGRESS)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            
            // 出票失败 -> 出票中：重试出票操作
            .withExternal()
                .source(OrderState.TICKETING_FAILED)
                .target(OrderState.TICKETING_IN_PROGRESS)
                .event(OrderEvent.RETRY_TICKETING)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and()
            
            // 出票失败 -> 已取消：取消出票失败的订单
            .withExternal()
                .source(OrderState.TICKETING_FAILED)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .guard(actionHandler::guard)
                .action(actionHandler::action)
                .and();
    }
}