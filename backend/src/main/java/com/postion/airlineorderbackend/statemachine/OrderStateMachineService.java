package com.postion.airlineorderbackend.statemachine;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单状态机服务
 * 提供统一的状态管理接口
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStateMachineService {

    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
    private final StateMachinePersister<OrderState, OrderEvent, String> stateMachinePersister;
    private final OrderRepository orderRepository;

    // 缓存状态机实例
    private final ConcurrentHashMap<String, StateMachine<OrderState, OrderEvent>> stateMachineCache = new ConcurrentHashMap<>();

    /**
     * 触发状态转换（无用户信息）
     */
    public boolean triggerStateTransition(Long orderId, OrderEvent event, Object message) {
        return triggerStateTransition(orderId, event, message, null, null);
    }

    /**
     * 触发状态转换（带用户信息）
     */
    public boolean triggerStateTransition(Long orderId, OrderEvent event, Object message, Long userId, String userRole) {
        try {
            String machineId = "order_" + orderId;
            StateMachine<OrderState, OrderEvent> stateMachine = getStateMachine(machineId, orderId);

            // 创建状态上下文
            OrderStateContext context = new OrderStateContext();
            context.setOrderId(orderId);
            context.setTriggeredEvent(event);

            // 创建消息，添加用户信息到消息头
            Message<OrderEvent> messageObj = MessageBuilder
                    .withPayload(event)
                    .setHeader("orderId", orderId)
                    .setHeader("context", context)
                    .setHeader("message", message)
                    .setHeader("userId", userId)
                    .setHeader("userRole", userRole)
                    .build();

            // 发送事件
            boolean result = stateMachine.sendEvent(messageObj);

            if (result) {
                // 持久化状态机状态
                stateMachinePersister.persist(stateMachine, machineId);
                
                // 同步更新数据库中的订单状态
                Order order = orderRepository.findById(orderId).orElse(null);
                if (order != null) {
                    OrderState newState = stateMachine.getState().getId();
                    order.setStatus(newState.name());
                    orderRepository.save(order);
                    log.info("状态转换成功: 订单={}, 事件={}, 新状态={}, 用户={}",
                            orderId, event, newState, userId);
                }
            } else {
                log.warn("状态转换失败: 订单={}, 事件={}, 当前状态={}, 用户={}",
                        orderId, event, stateMachine.getState().getId(), userId);
            }

            return result;

        } catch (Exception e) {
            log.error("状态转换异常: 订单={}, 事件={}, 用户={}", orderId, event, userId, e);
            return false;
        }
    }

    /**
     * 获取当前状态
     */
    public OrderState getCurrentState(Long orderId) {
        try {
            String machineId = "order_" + orderId;
            StateMachine<OrderState, OrderEvent> stateMachine = getStateMachine(machineId, orderId);
            return stateMachine.getState().getId();
        } catch (Exception e) {
            log.error("获取当前状态失败: 订单={}", orderId, e);
            return null;
        }
    }

    /**
     * 验证事件是否可以触发
     */
    public boolean canTriggerEvent(Long orderId, OrderEvent event) {
        try {
            OrderState currentState = getCurrentState(orderId);
            if (currentState == null) {
                return false;
            }

            // 基于状态机配置验证转换是否允许
            return isValidTransition(currentState, event);

        } catch (Exception e) {
            log.error("验证事件失败: 订单={}, 事件={}", orderId, event, e);
            return false;
        }
    }

    /**
     * 获取或创建状态机实例
     */
    private StateMachine<OrderState, OrderEvent> getStateMachine(String machineId, Long orderId) throws Exception {
        return stateMachineCache.computeIfAbsent(machineId, id -> {
            try {
                StateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory.getStateMachine(id);

                // 从数据库获取订单状态
                Order order = orderRepository.findById(orderId).orElse(null);
                if (order != null) {
                    OrderState currentState = OrderState.valueOf(order.getStatus());
                    
                    // 启动状态机
                    try {
                        stateMachine.start();
                    } catch (IllegalStateException e) {
                        // 状态机已经启动
                        log.debug("状态机已启动: {}", machineId);
                    }

                    // 尝试恢复之前的状态
                    try {
                        stateMachinePersister.restore(stateMachine, machineId);
                        // 确保状态机状态与数据库一致
                        if (!stateMachine.getState().getId().equals(currentState)) {
                            log.warn("状态机状态与数据库不一致，重新初始化: 状态机={}, 数据库={}", 
                                   stateMachine.getState().getId(), currentState);
                            stateMachine.stop();
                            stateMachine.start();
                        }
                    } catch (Exception e) {
                        // 如果恢复失败，使用当前数据库状态
                        log.debug("状态机恢复失败，使用数据库状态: {}", currentState);
                        // 设置正确的初始状态
                        stateMachine.stop();
                        stateMachine.start();
                    }
                } else {
                    // 如果订单不存在，启动状态机但不设置状态
                    try {
                        stateMachine.start();
                    } catch (IllegalStateException e) {
                        // 状态机已经启动
                        log.debug("状态机已启动: {}", machineId);
                    }
                }

                return stateMachine;
            } catch (Exception e) {
                log.error("创建状态机失败: {}", machineId, e);
                throw new RuntimeException("创建状态机失败", e);
            }
        });
    }

    /**
     * 验证状态转换是否有效
     */
    private boolean isValidTransition(OrderState currentState, OrderEvent event) {
        // 基于业务规则验证转换
        switch (currentState) {
            case PENDING_PAYMENT:
                return event == OrderEvent.PAY || event == OrderEvent.CANCEL || event == OrderEvent.AUTO_CANCEL;
            case PAID:
                return event == OrderEvent.PROCESS_TICKETING || event == OrderEvent.CANCEL;
            case TICKETING_IN_PROGRESS:
                return event == OrderEvent.TICKETING_SUCCESS ||
                        event == OrderEvent.TICKETING_FAILURE ||
                        event == OrderEvent.CANCEL;
            case TICKETING_FAILED:
                return event == OrderEvent.RETRY_TICKETING || event == OrderEvent.CANCEL;
            case TICKETED:
            case CANCELLED:
                return false; // 终止状态，不允许任何转换
            default:
                return false;
        }
    }

    /**
     * 清理状态机缓存
     */
    public void clearStateMachineCache(Long orderId) {
        String machineId = "order_" + orderId;
        stateMachineCache.remove(machineId);
        log.debug("清理状态机缓存: {}", machineId);
    }
}