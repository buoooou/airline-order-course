package com.postion.airlineorderbackend.statemachine;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 订单状态机动作处理器
 * 处理状态转换时的业务逻辑和副作用
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStateActionHandler {

    private final OrderRepository orderRepository;

    /**
     * 状态转换前的验证和处理
     */
    public boolean guard(org.springframework.statemachine.StateContext<OrderState, OrderEvent> context) {
        Message<OrderEvent> message = context.getMessage();
        if (message == null) {
            log.error("缺少消息信息");
            return false;
        }

        Long orderId = message.getHeaders().get("orderId", Long.class);
        if (orderId == null) {
            log.error("缺少订单ID信息");
            return false;
        }

        // 检查订单是否存在
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.error("订单不存在: {}", orderId);
            return false;
        }

        // 验证当前状态是否匹配转换的源状态
        OrderState fromState = context.getTransition().getSource().getId();
        if (!order.getStatus().equals(fromState.name())) {
            log.error("订单状态不匹配，期望: {}, 实际: {}", fromState, order.getStatus());
            return false;
        }

        // 权限验证：检查是否为admin或订单所有者
        Long userId = message.getHeaders().get("userId", Long.class);
        String userRole = message.getHeaders().get("userRole", String.class);
        
        if (userId == null || userRole == null) {
            log.error("缺少用户信息，userId: {}, userRole: {}", userId, userRole);
            return false;
        }

        // admin角色可以操作任何订单
        if ("admin".equalsIgnoreCase(userRole)) {
            return true;
        }

        // 订单所有者可以操作自己的订单
        if (userId.equals(order.getUserId())) {
            return true;
        }

        log.error("权限不足：用户 {} 无法操作订单 {}", userId, orderId);
        return false;
    }

    /**
     * 状态转换时的动作
     */
    public void action(org.springframework.statemachine.StateContext<OrderState, OrderEvent> context) {
        Message<OrderEvent> message = context.getMessage();
        if (message == null) {
            log.error("缺少消息信息");
            return;
        }

        Long orderId = message.getHeaders().get("orderId", Long.class);
        if (orderId == null) {
            log.error("缺少订单ID信息");
            return;
        }

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalStateException("订单不存在"));

            OrderState fromState = context.getTransition().getSource().getId();
            OrderState toState = context.getTransition().getTarget().getId();

            // 更新订单状态
            order.setStatus(toState.name());
            orderRepository.save(order);

            // 记录状态转换日志
            log.info("订单状态转换: orderId={}, {} -> {}, 事件: {}", 
                    orderId, fromState, toState, context.getEvent());

            // 执行状态特定的业务逻辑
            OrderStateContext orderContext = new OrderStateContext(order);
            executeStateSpecificLogic(orderContext, toState);

        } catch (Exception e) {
            log.error("状态转换处理失败: {}", orderId, e);
            throw new RuntimeException("状态转换失败", e);
        }
    }

    /**
     * 执行状态特定的业务逻辑
     */
    private void executeStateSpecificLogic(OrderStateContext context, OrderState newState) {
        switch (newState) {
            case PAID:
                handlePaidState(context);
                break;
            case TICKETING_IN_PROGRESS:
                handleTicketingInProgressState(context);
                break;
            case TICKETED:
                handleTicketedState(context);
                break;
            case CANCELLED:
                handleCancelledState(context);
                break;
            case TICKETING_FAILED:
                handleTicketingFailedState(context);
                break;
            default:
                log.debug("无需处理的状态: {}", newState);
        }
    }

    private void handlePaidState(OrderStateContext context) {
        log.info("订单已支付，准备出票: {}", context.getOrderNumber());
        // 可以触发异步出票流程
    }

    private void handleTicketingInProgressState(OrderStateContext context) {
        log.info("开始处理出票: {}", context.getOrderNumber());
        // 可以调用外部出票系统
    }

    private void handleTicketedState(OrderStateContext context) {
        log.info("订单出票成功: {}", context.getOrderNumber());
        // 可以发送成功通知
    }

    private void handleCancelledState(OrderStateContext context) {
        log.info("订单已取消: {}", context.getOrderNumber());
        // 可以触发退款流程
    }

    private void handleTicketingFailedState(OrderStateContext context) {
        log.warn("订单出票失败: {}", context.getOrderNumber());
        // 可以记录失败原因，准备重试
    }
}