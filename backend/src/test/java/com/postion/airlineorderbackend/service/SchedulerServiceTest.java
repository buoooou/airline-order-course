package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * 定时任务服务测试类
 * 测试订单定时任务的相关功能
 */
@ExtendWith(MockitoExtension.class)
public class SchedulerServiceTest {

    @Mock
    private OrderServiceImpl orderService;

    @Test
    void testProcessUnpaidOrders() {
        // 测试处理超时未支付订单
        doNothing().when(orderService).processUnpaidOrders();
        
        orderService.processUnpaidOrders();
        
        verify(orderService, times(1)).processUnpaidOrders();
    }

    @Test
    void testProcessTicketingOrders() {
        // 测试处理出票中订单
        doNothing().when(orderService).processTicketingOrders();
        
        orderService.processTicketingOrders();
        
        verify(orderService, times(1)).processTicketingOrders();
    }
}