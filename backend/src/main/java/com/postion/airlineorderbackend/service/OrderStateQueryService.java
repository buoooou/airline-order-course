package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.entity.OrderStateHistory;
import com.postion.airlineorderbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderStateQueryService {

    private final OrderStateHistoryService orderStateHistoryService;
    private final OrderRepository orderRepository;

    /**
     * 查询订单的完整状态转换历史
     */
    public List<OrderStateHistory> getOrderStateHistory(Long orderId) {
        return orderStateHistoryService.getOrderStateHistory(orderId);
    }

    /**
     * 查询订单的失败状态转换记录
     */
    public List<OrderStateHistory> getFailedTransitions(Long orderId) {
        return orderStateHistoryService.getFailedTransitions(orderId);
    }

    /**
     * 按订单号查询状态转换历史
     */
    public List<OrderStateHistory> getStateHistoryByOrderNumber(String orderNumber) {
        return orderStateHistoryService.getStateHistoryByOrderNumber(orderNumber);
    }

    /**
     * 统计订单的状态转换统计信息
     */
    public Map<String, Long> getOrderTransitionStats(Long orderId) {
        List<OrderStateHistory> history = getOrderStateHistory(orderId);
        
        Map<String, Long> stats = history.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getSuccess() ? "success" : "failed",
                        Collectors.counting()
                ));
        
        return stats;
    }

    /**
     * 获取最近的状态转换记录
     */
    public OrderStateHistory getLatestTransition(Long orderId) {
        List<OrderStateHistory> history = getOrderStateHistory(orderId);
        return history.isEmpty() ? null : history.get(0);
    }

    /**
     * 获取最近的失败状态转换记录
     */
    public OrderStateHistory getLatestFailedTransition(Long orderId) {
        List<OrderStateHistory> failedTransitions = getFailedTransitions(orderId);
        return failedTransitions.isEmpty() ? null : failedTransitions.get(0);
    }

    /**
     * 判断订单是否有失败的状态转换记录
     */
    public boolean hasFailedTransitions(Long orderId) {
        return !getFailedTransitions(orderId).isEmpty();
    }
}