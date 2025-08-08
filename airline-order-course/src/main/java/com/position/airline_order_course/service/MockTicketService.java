package com.position.airline_order_course.service;

import java.util.List;

import com.position.airline_order_course.dto.TicketResponse;

/*
 * 出票服务接口
 */
public interface MockTicketService {

    TicketResponse<String> issueTicket(String orderId);

    TicketResponse<List<String>> getPendingTasks();

    TicketResponse<String> retryTicket(String orderId);
}