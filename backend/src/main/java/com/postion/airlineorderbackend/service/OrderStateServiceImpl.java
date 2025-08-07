package com.postion.airlineorderbackend.service;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.adapter.AirlineApi;
import com.postion.airlineorderbackend.entity.OrderStatus;
import com.postion.airlineorderbackend.exception.AirlineApiException;

@Service
public class OrderStateServiceImpl implements IOrderStateService {

    private AirlineApi airlineApi = null;

    // 通过构造函数注入依赖
    public void OrderServiceImpl(AirlineApi airlineApi) {
        this.airlineApi = airlineApi;
    }

    public void processOrder(Long orderId) {
        try {
            String ticketNumber = airlineApi.issueTicket(orderId);
            // 处理出票成功后的业务逻辑...
            System.out.println("订单 " + orderId + " 最终处理成功，获得票号: " + ticketNumber);
        } catch (InterruptedException e) {
            // 处理线程中断异常
            System.err.println("处理订单 " + orderId + " 时线程被中断。");
        } catch (AirlineApiException e) {
            // 处理航司接口异常
            System.err.println("处理订单 " + orderId + " 失败，原因：" + e.getMessage());
            // 这里可以进行失败重试、发送通知或回滚操作
        }
    }
    
    @Override
    public boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case PENDING_PAYMENT:
                return next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID:
                return next == OrderStatus.TICKETING_IN_PROGRESS || next == OrderStatus.CANCELLED;
            case TICKETING_IN_PROGRESS:
                return next == OrderStatus.TICKETED || next == OrderStatus.TICKETING_FAILED || next == OrderStatus.CANCELLED;
            case TICKETING_FAILED:
                return next == OrderStatus.TICKETING_IN_PROGRESS || next == OrderStatus.CANCELLED;
            case TICKETED:
                return next == OrderStatus.CANCELLED;
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    @Override
    public OrderStatus updateStatus(OrderStatus current, OrderStatus next) {
        if (!isValidTransition(current, next)) {
            throw new IllegalStateException("Invalid state transition: " + current + " -> " + next);
        }
        return next;
    }
}
