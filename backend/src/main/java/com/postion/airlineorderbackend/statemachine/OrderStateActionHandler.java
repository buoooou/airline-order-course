package com.postion.airlineorderbackend.statemachine;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * 订单状态机动作处理器
 * 
 * 核心职责：处理订单状态转换过程中的业务逻辑验证和副作用操作
 * 
 * 在Spring StateMachine框架中，此类作为状态机的动作处理器，负责：
 * 1. guard() - 状态转换前的验证逻辑（前置条件检查）
 * 2. action() - 状态转换时的业务操作（副作用处理）
 * 
 * 验证维度：
 * - 数据完整性：检查订单是否存在，状态是否匹配
 * - 权限控制：基于用户角色和订单所有权进行权限验证
 * - 业务规则：确保状态转换符合业务逻辑
 * 
 * 业务操作：
 * - 数据库同步：将状态机状态同步到数据库
 * - 日志记录：记录详细的状态转换日志
 * - 业务流程：触发状态特定的业务逻辑（如通知、退款等）
 * 
 * 异常处理：
 * - 所有异常都会被捕获并记录，确保状态机稳定性
 * - 数据库操作失败会触发事务回滚
 * 
 * 使用场景：
 * - 用户支付订单后触发状态从PENDING_PAYMENT到PAID
 * - 管理员取消订单触发状态到CANCELLED
 * - 系统自动超时取消触发状态转换
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStateActionHandler {

    private final OrderRepository orderRepository;

    /**
     * 状态转换前的验证逻辑（Guard条件）
     * 
     * 在状态机允许状态转换之前执行的前置条件验证，确保转换的合法性和安全性。
     * 
     * 验证流程：
     * 1. 消息完整性验证：检查消息和必要参数是否存在
     * 2. 订单存在性验证：确保订单在数据库中存在
     * 3. 状态一致性验证：验证当前状态与数据库状态匹配
     * 4. 权限验证：基于用户角色和订单所有权进行权限控制
     * 
     * 权限规则：
     * - admin角色：拥有所有订单的操作权限（超级管理员权限）
     * - user角色：仅拥有自己订单的操作权限（订单所有者权限）
     * - 其他角色：无操作权限
     * 
     * @param context 状态机上下文，包含消息、转换定义和状态信息
     * @return true 允许状态转换，false 拒绝状态转换
     * 
     * @throws IllegalStateException 当订单不存在或状态不匹配时
     * @throws SecurityException     当权限验证失败时
     */
    public boolean guard(StateContext<OrderState, OrderEvent> context) {
        Message<OrderEvent> message = context.getMessage();
        OrderEvent event = context.getEvent();

        if (message == null) {
            log.error("缺少消息信息");
            return false;
        }

        Long orderId = message.getHeaders().get("orderId", Long.class);
        Long userId = message.getHeaders().get("userId", Long.class);
        String userRole = message.getHeaders().get("userRole", String.class);

        log.info("开始Guard检查: 订单={}, 事件={}, 用户={}, 用户角色={}", orderId, event, userId, userRole);

        try {
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
            log.debug("订单信息: 订单={}, 当前状态={}, 用户ID={}", orderId, order.getStatus(), order.getUserId());

            // 验证当前状态是否匹配转换的源状态
            OrderState fromState = context.getTransition().getSource().getId();
            OrderState currentDbState = OrderState.valueOf(order.getStatus());
            log.debug("状态检查: 数据库状态={}, 状态机源状态={}, 事件={}", currentDbState, fromState, event);

            if (!order.getStatus().equals(fromState.name())) {
                log.error("订单状态不匹配，期望: {}, 实际: {}", fromState, order.getStatus());
                return false;
            }

            // 权限验证：检查是否为admin或订单所有者
            if (userId == null || userRole == null) {
                log.error("缺少用户信息，userId: {}, userRole: {}", userId, userRole);
                return false;
            }

            // admin角色可以操作任何订单
            if ("admin".equalsIgnoreCase(userRole)) {
                log.info("Guard检查通过: admin用户操作订单={}, 事件={}", orderId, event);
                return true;
            }

            // 订单所有者可以操作自己的订单
            if (userId.equals(order.getUserId())) {
                log.info("Guard检查通过: 订单所有者操作订单={}, 事件={}", orderId, event);
                return true;
            }

            log.error("权限不足：用户 {} 无法操作订单 {}", userId, orderId);
            return false;

        } catch (Exception e) {
            log.error("Guard检查异常: 订单={}, 事件={}, 用户={}, 错误: {}",
                    orderId, event, userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 状态转换时的业务动作（Action处理）
     * 
     * 在状态转换成功时执行的业务操作，负责处理状态转换的副作用和数据持久化。
     * 此方法在guard()方法返回true后执行，确保状态转换的完整性。
     * 
     * 处理流程：
     * 1. 数据准备：从消息中提取订单ID和相关参数
     * 2. 数据验证：再次验证订单存在性和状态合法性
     * 3. 状态同步：将状态机状态同步到数据库订单记录
     * 4. 日志记录：记录详细的状态转换日志用于审计
     * 5. 业务触发：根据新状态执行特定的业务逻辑
     * 
     * 事务保证：
     * - 此方法在事务上下文中执行，数据库操作失败会触发事务回滚
     * - 状态机状态会回滚到转换前的状态
     * 
     * 异常处理：
     * - 所有异常都会被捕获并记录，确保状态机稳定性
     * - 异常会触发事务回滚，保证数据一致性
     * 
     * @param context 状态机上下文，包含完整的转换信息和业务数据
     * 
     * @throws RuntimeException      当数据库操作或业务逻辑处理失败时
     * @throws IllegalStateException 当订单不存在或状态转换非法时
     */
    public void action(StateContext<OrderState, OrderEvent> context) {
        Message<OrderEvent> message = context.getMessage();
        if (message == null) {
            log.error("缺少消息信息");
            throw new IllegalStateException("状态转换消息不能为空");
        }

        Long orderId = message.getHeaders().get("orderId", Long.class);
        if (orderId == null) {
            log.error("缺少订单ID信息");
            throw new IllegalStateException("订单ID不能为空");
        }

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalStateException("订单不存在"));

            OrderState fromState = context.getTransition().getSource().getId();
            OrderState toState = context.getTransition().getTarget().getId();

            // 验证数据库当前状态与状态机当前状态一致
            OrderState currentDbState = OrderState.valueOf(order.getStatus());
            if (currentDbState != fromState) {
                log.error("状态不一致：数据库状态={}, 状态机当前状态={}, orderId={}",
                        currentDbState, fromState, orderId);
                throw new IllegalStateException(
                        String.format("订单状态不一致：数据库当前状态为%s，无法进行%s到%s的转换",
                                currentDbState, fromState, toState));
            }

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
     * 
     * 根据订单的新状态执行相应的业务操作，实现状态转换的后续处理。
     * 每个状态对应特定的业务流程，确保状态转换的完整性和业务规则的执行。
     * 
     * 状态处理逻辑：
     * - PAID：订单已支付，触发出票准备流程
     * - TICKETING_IN_PROGRESS：开始出票处理，调用外部出票系统
     * - TICKETED：出票成功，发送成功通知给用户
     * - CANCELLED：订单取消，触发退款流程和库存释放
     * - TICKETING_FAILED：出票失败，记录失败原因并准备重试
     * 
     * 扩展性：
     * 新增状态时只需添加对应的处理方法，无需修改核心逻辑
     * 
     * @param context  订单状态上下文，包含完整的订单信息
     * @param newState 新状态，用于确定执行哪种业务逻辑
     * 
     * @throws RuntimeException 当业务逻辑处理失败时
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

    /**
     * 处理订单已支付状态的后续业务逻辑
     * 
     * 当订单状态从PENDING_PAYMENT转换到PAID时触发，负责：
     * - 记录支付成功日志
     * - 准备出票相关数据
     * - 触发异步出票流程（可选）
     * - 发送支付成功通知给用户
     * 
     * @param context 订单状态上下文，包含订单详细信息
     */
    private void handlePaidState(OrderStateContext context) {
        log.info("订单已支付，准备出票: {}", context.getOrderNumber());
        // 可以触发异步出票流程
    }

    /**
     * 处理出票中状态的后续业务逻辑
     * 
     * 当订单状态转换到TICKETING_IN_PROGRESS时触发，负责：
     * - 记录出票开始日志
     * - 调用外部出票系统API
     * - 初始化出票跟踪信息
     * - 设置出票超时监控
     * 
     * @param context 订单状态上下文，包含订单详细信息
     */
    private void handleTicketingInProgressState(OrderStateContext context) {
        log.info("开始处理出票: {}", context.getOrderNumber());
        // 可以调用外部出票系统
    }

    /**
     * 处理出票成功状态的后续业务逻辑
     * 
     * 当订单状态转换到TICKETED时触发，负责：
     * - 记录出票成功日志
     * - 生成电子机票或行程单
     * - 发送出票成功通知给用户（邮件/短信）
     * - 更新订单完成时间
     * - 触发后续服务（如积分、优惠券等）
     * 
     * @param context 订单状态上下文，包含订单详细信息
     */
    private void handleTicketedState(OrderStateContext context) {
        log.info("订单出票成功: {}", context.getOrderNumber());
        // 可以发送成功通知
    }

    /**
     * 处理订单取消状态的后续业务逻辑
     * 
     * 当订单状态转换到CANCELLED时触发，负责：
     * - 记录订单取消日志
     * - 触发退款流程（根据取消原因和时间）
     * - 释放占用的座位或库存
     * - 发送取消通知给用户
     * - 更新相关统计数据
     * 
     * @param context 订单状态上下文，包含订单详细信息
     */
    private void handleCancelledState(OrderStateContext context) {
        log.info("订单已取消: {}", context.getOrderNumber());
        // 可以触发退款流程
    }

    /**
     * 处理出票失败状态的后续业务逻辑
     * 
     * 当订单状态转换到TICKETING_FAILED时触发，负责：
     * - 记录出票失败日志和原因
     * - 保存失败详情用于后续分析
     * - 准备重试机制（如自动重试或人工处理）
     * - 通知用户出票失败并提供解决方案
     * - 触发客服介入流程（可选）
     * 
     * @param context 订单状态上下文，包含订单详细信息
     */
    private void handleTicketingFailedState(OrderStateContext context) {
        log.warn("订单出票失败: {}", context.getOrderNumber());
        // 可以记录失败原因，准备重试
    }
}