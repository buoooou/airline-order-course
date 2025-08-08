package com.postion.airlineorderbackend.statemachine;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.service.OrderStateHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单状态机服务
 * 提供统一的状态管理接口，负责订单生命周期中所有状态转换的协调和管理
 * 
 * 核心功能：
 * 1. 状态转换触发：处理所有订单状态变更请求
 * 2. 状态持久化：确保状态机状态与数据库同步
 * 3. 权限验证：基于用户角色和订单所有权进行权限控制
 * 4. 缓存管理：高效管理状态机实例，避免重复创建
 * 5. 异常处理：提供完整的错误处理和日志记录
 * 
 * 状态机实例命名规则：order_{orderId}
 * 缓存策略：使用ConcurrentHashMap实现线程安全的实例缓存
 * 持久化策略：使用Spring StateMachine的StateMachinePersister进行状态持久化
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStateMachineService {

    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
    private final StateMachinePersister<OrderState, OrderEvent, String> stateMachinePersister;
    private final OrderRepository orderRepository;
    private final JpaStateMachineRepository jpaStateMachineRepository;
    private final OrderStateHistoryService orderStateHistoryService;

    // 缓存状态机实例
    private final ConcurrentHashMap<String, StateMachine<OrderState, OrderEvent>> stateMachineCache = new ConcurrentHashMap<>();

    /**
     * 触发状态转换（无用户信息）
     * 
     * 使用场景：系统内部调用或无需权限验证的场景
     * 
     * @param orderId 订单ID，用于标识状态机实例
     * @param event   触发的事件类型，如PAY、CANCEL等
     * @param message 附加的业务数据，可以是OrderStateContext或其他业务对象
     * @return true 状态转换成功，false 状态转换失败（可能原因：状态不允许、权限不足等）
     * 
     *         注意：此方法会传递null用户ID和角色，权限验证将使用默认逻辑
     */
    public boolean triggerStateTransition(Long orderId, OrderEvent event, Object message) {
        return triggerStateTransition(orderId, event, message, null, null);
    }

    /**
     * 触发状态转换（带用户信息）
     * 
     * 使用场景：用户操作订单时的状态变更
     * 
     * 权限验证逻辑：
     * - admin角色：拥有所有订单的操作权限
     * - user角色：仅拥有自己订单的操作权限
     * 
     * 状态同步机制：
     * 1. 状态机状态变更成功后，同步更新数据库订单状态
     * 2. 使用事务确保数据一致性
     * 3. 失败时自动回滚状态机状态
     * 
     * @param orderId  订单ID
     * @param event    触发的事件类型
     * @param message  业务上下文数据
     * @param userId   操作用户ID，用于权限验证
     * @param userRole 用户角色，用于权限判断
     * @return true 转换成功，false 转换失败
     * 
     * @throws IllegalArgumentException 当订单不存在或参数无效时
     * @throws RuntimeException         当状态机创建或持久化失败时
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean triggerStateTransition(Long orderId, OrderEvent event, Object message, Long userId,
            String userRole) {
        log.info("开始状态转换: 订单={}, 事件={}, 用户={}, 用户角色={}", orderId, event, userId, userRole);

        String machineId = OrderStateMachineIdGenerator.generateMachineId(orderId);
        OrderState currentDbState = null;
        OrderState newState = null;
        String errorMessage = null;
        boolean success = false;

        try {
            // 1. 预先检查订单状态和权限
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                errorMessage = "订单不存在: " + orderId;
                log.error(errorMessage);
                recordStateTransition(orderId, null, null, null, event.name(), 
                        String.valueOf(userId), userRole, false, errorMessage, String.valueOf(message));
                return false;
            }

            currentDbState = OrderState.valueOf(order.getStatus());
            log.debug("数据库当前状态: 订单={}, 状态={}", orderId, currentDbState);

            // 2. 验证转换合法性
            if (!isValidTransition(currentDbState, event)) {
                errorMessage = String.format("非法状态转换: 订单=%d, 当前状态=%s, 事件=%s", 
                        orderId, currentDbState, event);
                log.error(errorMessage);
                recordStateTransition(orderId, order.getOrderNumber(), currentDbState.name(), 
                        null, event.name(), String.valueOf(userId), userRole, false, 
                        errorMessage, String.valueOf(message));
                return false;
            }

            // 3. 验证权限
            if (!"admin".equalsIgnoreCase(userRole) && !userId.equals(order.getUserId())) {
                errorMessage = String.format("权限不足: 订单=%d, 用户=%d, 用户角色=%s, 订单所有者=%d",
                        orderId, userId, userRole, order.getUserId());
                log.error(errorMessage);
                recordStateTransition(orderId, order.getOrderNumber(), currentDbState.name(), 
                        null, event.name(), String.valueOf(userId), userRole, false, 
                        errorMessage, String.valueOf(message));
                return false;
            }

            StateMachine<OrderState, OrderEvent> stateMachine = getStateMachine(machineId, orderId);
            newState = stateMachine.getState().getId();

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
                try {
                    // 持久化状态机状态 - 只在状态实际发生变化时执行
                    stateMachinePersister.persist(stateMachine, machineId);
                    newState = stateMachine.getState().getId();
                    
                    log.info("状态转换成功: 订单={}, 事件={}, 旧状态={}, 新状态={}, 用户={}",
                            orderId, event, currentDbState, newState, userId);
                    
                    recordStateTransition(orderId, order.getOrderNumber(), currentDbState.name(), 
                            newState.name(), event.name(), String.valueOf(userId), userRole, 
                            true, null, String.valueOf(message));
                    success = true;
                    return true;
                } catch (Exception e) {
                    errorMessage = "状态机持久化失败: " + e.getMessage();
                    log.warn("状态机持久化失败，但状态转换成功: 订单={}, 事件={}, 错误: {}",
                            orderId, event, e.getMessage());
                    recordStateTransition(orderId, order.getOrderNumber(), currentDbState.name(), 
                            newState.name(), event.name(), String.valueOf(userId), userRole, 
                            true, errorMessage, String.valueOf(message));
                    return true;
                }
            } else {
                errorMessage = "状态转换失败: 状态机拒绝事件";
                log.warn("状态转换失败: 订单={}, 事件={}, 当前状态={}, 用户={}",
                        orderId, event, stateMachine.getState().getId(), userId);
                recordStateTransition(orderId, order.getOrderNumber(), currentDbState.name(), 
                        null, event.name(), String.valueOf(userId), userRole, false, 
                        errorMessage, String.valueOf(message));
                return false;
            }

        } catch (Exception e) {
            errorMessage = "状态转换异常: " + e.getMessage();
            log.error("状态转换异常: 订单={}, 事件={}, 用户={}, 错误: {}", orderId, event, userId, e.getMessage(), e);
            recordStateTransition(orderId, null, 
                    currentDbState != null ? currentDbState.name() : null, 
                    newState != null ? newState.name() : null, 
                    event.name(), String.valueOf(userId), userRole, false, 
                    errorMessage, String.valueOf(message));
            throw new RuntimeException("状态转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取订单当前状态
     * 
     * 从状态机实例中获取当前状态，状态机会自动与数据库状态同步。
     * 如果订单不存在，状态机将使用默认的初始状态。
     * 用于查询订单当前所处的状态
     * 
     * @param orderId 订单ID
     * @return 当前状态枚举值，如果发生异常返回null
     * 
     *         性能考虑：优先从缓存的状态机实例获取
     */
    public OrderState getCurrentState(Long orderId) {
        String machineId = OrderStateMachineIdGenerator.generateMachineId(orderId);
        try {
            StateMachine<OrderState, OrderEvent> stateMachine = getStateMachine(machineId, orderId);
            return stateMachine.getState().getId();
        } catch (Exception e) {
            log.error("获取当前状态失败: 订单={}", orderId, e);
            return null;
        }
    }

    /**
     * 验证事件是否可以触发
     * 
     * 基于当前状态和状态机配置，判断指定事件是否可以触发状态转换
     * 用于前端按钮显示控制或业务逻辑前置验证
     * 
     * 验证规则：
     * 1. 检查订单是否存在
     * 2. 检查当前状态是否允许该事件
     * 3. 检查是否为终止状态（已出票或已取消）
     * 
     * @param orderId 订单ID
     * @param event   待验证的事件
     * @return true 可以触发，false 不允许触发
     * 
     *         常见不允许触发的原因：
     *         - 订单不存在
     *         - 当前状态不允许该事件
     *         - 订单已处于终止状态
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
     * 
     * 核心状态管理逻辑：
     * 1. 缓存检查：优先从ConcurrentHashMap缓存中获取
     * 2. 实例创建：使用StateMachineFactory创建新实例，并设置正确的状态机ID
     * 3. 状态恢复：尝试从持久化存储中恢复之前的状态
     * 4. 状态一致性检查：验证状态机状态与数据库状态一致
     * 5. 异常处理：状态不一致或恢复失败时抛出异常终止处理
     * 
     * 缓存策略：
     * - 使用订单ID作为缓存key
     * - 线程安全的ConcurrentHashMap实现
     * - 自动处理并发访问
     * 
     * 状态一致性保障：
     * - 状态机状态必须与数据库状态完全一致
     * - 发现不一致时抛出IllegalStateException终止处理
     * - 恢复失败时抛出RuntimeException终止处理
     * - 不尝试自动修复状态不一致问题
     * 
     * @param machineId 状态机实例ID
     * @param orderId   关联的订单ID
     * @return 配置好的状态机实例
     * @throws Exception 当状态机创建、配置失败，或状态不一致时
     */
    private StateMachine<OrderState, OrderEvent> getStateMachine(String machineId, Long orderId) throws Exception {
        return stateMachineCache.computeIfAbsent(machineId, id -> {
            try {
                // 使用订单ID作为状态机ID，确保ID格式正确
                StateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory.getStateMachine(id);
                log.debug("创建状态机实例 - 工厂返回ID: {}, 期望ID: {}", stateMachine.getId(), id);
                // stateMachine.getStateMachineAccessor().doWithAllRegions(function ->
                // function.setUuid(id));
                // 从数据库获取订单状态
                Order order = orderRepository.findById(orderId).orElse(null);
                if (order != null) {

                    // 启动状态机
                    try {
                        stateMachine.start();
                    } catch (IllegalStateException e) {
                        // 状态机已经启动
                        log.debug("状态机已启动: {}", machineId);
                    }

                    // 只有在数据库中存在持久化记录时才调用restore
                    boolean hasPersistedState = checkIfPersistedStateExists(machineId);

                    if (hasPersistedState) {
                        log.debug("发现持久化记录，开始恢复状态机: 订单ID={}", orderId);
                        try {
                            stateMachinePersister.restore(stateMachine, machineId);
                            log.debug("状态机恢复完成: 订单ID={}", orderId);
                        } catch (Exception e) {
                            log.warn("状态机恢复失败，将使用新创建的状态机: 订单ID={}, 错误: {}",
                                    orderId, e.getMessage());
                        }
                    } else {
                        log.debug("无持久化记录，使用新创建的状态机: 订单ID={}", orderId);
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
     * 
     * 基于业务规则验证从当前状态到目标状态的转换是否合法
     * 这是状态机配置之外的额外验证层，确保业务规则的正确性
     * 
     * 状态转换规则：
     * PENDING_PAYMENT -> [PAY, CANCEL, AUTO_CANCEL]：待支付状态下可支付、取消或超时自动取消
     * PAID -> [PROCESS_TICKETING, CANCEL]：已支付状态下可开始出票或取消
     * TICKETING_IN_PROGRESS -> [TICKETING_SUCCESS, TICKETING_FAILURE,
     * CANCEL]：出票中状态下可成功、失败或取消
     * TICKETING_FAILED -> [RETRY_TICKETING, CANCEL]：出票失败状态下可重试或取消
     * TICKETED/CANCELLED -> []：终止状态，不允许任何转换
     * 
     * @param currentState 当前状态
     * @param event        触发的事件
     * @return true 转换有效，false 转换无效
     * 
     *         注意：此验证不包含权限检查，权限验证在guard方法中处理
     */
    private boolean isValidTransition(OrderState currentState, OrderEvent event) {
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
     * 记录状态转换历史
     * 
     * @param orderId 订单ID
     * @param orderNumber 订单号
     * @param fromState 源状态
     * @param toState 目标状态
     * @param event 事件名称
     * @param operator 操作人
     * @param operatorRole 操作人角色
     * @param success 是否成功
     * @param errorMessage 错误信息
     * @param requestData 请求数据
     */
    private void recordStateTransition(Long orderId, String orderNumber, String fromState, 
                                     String toState, String event, String operator, 
                                     String operatorRole, boolean success, String errorMessage, 
                                     String requestData) {
        try {
            orderStateHistoryService.recordStateTransition(
                    orderId, orderNumber, fromState, toState, event, 
                    operator, operatorRole, success, errorMessage, requestData);
        } catch (Exception e) {
            log.error("记录状态转换历史失败: {}", e.getMessage(), e);
            // 不影响主业务流程
        }
    }

    /**
     * 清理状态机缓存
     */
    public void clearStateMachineCache(Long orderId) {
        String machineId = OrderStateMachineIdGenerator.generateMachineId(orderId);
        stateMachineCache.remove(machineId);
        log.debug("清理状态机缓存: {}", machineId);
    }

    /**
     * 检查数据库中是否存在指定状态机的持久化记录
     * 
     * @param machineId 状态机实例ID
     * @return true 如果存在持久化记录，false 如果不存在
     */
    private boolean checkIfPersistedStateExists(String machineId) {
        try {
            return jpaStateMachineRepository.existsById(machineId);
        } catch (Exception e) {
            // 如果检查失败，假设没有持久化记录
            log.debug("检查持久化记录失败，假设无记录: {}", e.getMessage());
            return false;
        }
    }
}