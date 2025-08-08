package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.entity.OrderStateHistory;
import com.postion.airlineorderbackend.repository.OrderStateHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderStateHistoryService {

    private final OrderStateHistoryRepository orderStateHistoryRepository;

    /**
     * 记录状态转换历史
     * 
     * @param orderId 订单ID
     * @param orderNumber 订单号
     * @param fromState 源状态
     * @param toState 目标状态
     * @param event 触发事件
     * @param operator 操作人
     * @param operatorRole 操作人角色
     * @param success 是否成功
     * @param errorMessage 错误信息（失败时）
     * @param requestData 请求数据
     */
    @Transactional
    public void recordStateTransition(Long orderId, String orderNumber, String fromState, 
                                    String toState, String event, String operator, 
                                    String operatorRole, Boolean success, String errorMessage, 
                                    String requestData) {
        try {
            OrderStateHistory history = new OrderStateHistory();
            history.setOrderId(orderId);
            history.setOrderNumber(orderNumber);
            history.setFromState(fromState);
            history.setToState(toState);
            history.setEvent(event);
            history.setOperator(operator);
            history.setOperatorRole(operatorRole);
            history.setSuccess(success);
            history.setErrorMessage(errorMessage);
            history.setRequestData(requestData);
            history.setCreatedAt(LocalDateTime.now());

            orderStateHistoryRepository.save(history);
            log.debug("状态转换记录已保存: 订单={}, 事件={}, 成功={}", orderNumber, event, success);

        } catch (Exception e) {
            log.error("保存状态转换记录失败: {}", e.getMessage(), e);
            // 不抛出异常，避免影响主业务流程
        }
    }

    /**
     * 获取订单的状态转换历史
     */
    public List<OrderStateHistory> getOrderStateHistory(Long orderId) {
        return orderStateHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    /**
     * 获取订单的失败状态转换记录
     */
    public List<OrderStateHistory> getFailedTransitions(Long orderId) {
        return orderStateHistoryRepository.findFailedTransitionsByOrderId(orderId);
    }

    /**
     * 获取所有失败的状态转换记录
     */
    public List<OrderStateHistory> getAllFailedTransitions() {
        return orderStateHistoryRepository.findBySuccessOrderByCreatedAtDesc(false);
    }

    /**
     * 按订单号查询状态转换历史
     */
    public List<OrderStateHistory> getStateHistoryByOrderNumber(String orderNumber) {
        return orderStateHistoryRepository.findByOrderNumberOrderByCreatedAtDesc(orderNumber);
    }
}