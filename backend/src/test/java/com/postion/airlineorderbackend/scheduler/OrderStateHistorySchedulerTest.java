package com.postion.airlineorderbackend.scheduler;

import com.postion.airlineorderbackend.entity.OrderStateHistory;
import com.postion.airlineorderbackend.repository.OrderStateHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * 订单状态历史定时任务测试类
 * 验证定时任务的行为
 */
@ExtendWith(MockitoExtension.class)
class OrderStateHistorySchedulerTest {

    @Mock
    private OrderStateHistoryRepository orderStateHistoryRepository;

    @InjectMocks
    private OrderStateHistoryScheduler orderStateHistoryScheduler;

    @Test
    void testQueryFailedStateTransitions_WithFailedRecords() {
        // 创建模拟的失败记录
        OrderStateHistory failedRecord = new OrderStateHistory();
        failedRecord.setId(1L);
        failedRecord.setOrderId(1001L);
        failedRecord.setFromState("PAID");
        failedRecord.setToState("TICKETED");
        failedRecord.setSuccess(false);
        failedRecord.setErrorMessage("出票服务超时");
        failedRecord.setCreatedAt(LocalDateTime.now());

        // 模拟服务返回失败记录
        when(orderStateHistoryRepository.findBySuccessOrderByCreatedAtDesc(false))
                .thenReturn(Arrays.asList(failedRecord));

        // 执行定时任务
        orderStateHistoryScheduler.queryFailedStateTransitions();

        // 验证服务被调用
        verify(orderStateHistoryRepository, times(1)).findBySuccessOrderByCreatedAtDesc(false);
    }

    @Test
    void testQueryFailedStateTransitions_WithoutFailedRecords() {
        // 模拟服务返回空列表
        when(orderStateHistoryRepository.findBySuccessOrderByCreatedAtDesc(false))
                .thenReturn(Collections.emptyList());

        // 执行定时任务
        orderStateHistoryScheduler.queryFailedStateTransitions();

        // 验证服务被调用
        verify(orderStateHistoryRepository, times(1)).findBySuccessOrderByCreatedAtDesc(false);
    }

    // @Test
    // void testAnalyzeFailedTransitions_WithRecentFailures() {
    // // 创建模拟的失败记录
    // OrderStateHistory failedRecord = new OrderStateHistory();
    // failedRecord.setId(2L);
    // failedRecord.setOrderId(1002L);
    // failedRecord.setFromState("TICKETING_IN_PROGRESS");
    // failedRecord.setToState("TICKETED");
    // failedRecord.setSuccess(false);
    // failedRecord.setErrorMessage("外部API调用失败");
    // failedRecord.setCreatedAt(LocalDateTime.now());

    // // 模拟服务返回最近失败记录
    // when(orderStateHistoryRepository.findBySuccessOrderByCreatedAtDesc(false))
    // .thenReturn(Arrays.asList(failedRecord));

    // // 执行定时任务
    // orderStateHistoryScheduler.analyzeFailedTransitions();

    // // 验证服务被调用
    // verify(orderStateHistoryRepository,
    // times(1)).findBySuccessOrderByCreatedAtDesc(false);
    // }

    // @Test
    // void testAnalyzeFailedTransitions_WithoutRecentFailures() {
    // // 模拟服务返回空列表
    // when(orderStateHistoryRepository.findBySuccessOrderByCreatedAtDesc(false))
    // .thenReturn(Collections.emptyList());

    // // 执行定时任务
    // orderStateHistoryScheduler.analyzeFailedTransitions();

    // // 验证服务被调用
    // verify(orderStateHistoryRepository,
    // times(1)).findBySuccessOrderByCreatedAtDesc(false);
    // }
}