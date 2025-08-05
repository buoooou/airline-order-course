package com.position.airlineorderbackend.client;

import com.position.airlineorderbackend.dto.TicketingRequest;
import com.position.airlineorderbackend.dto.TicketingResponse;

/**
 * 航空公司出票系统客户端接口
 */
public interface TicketingClient {
    
    /**
     * 出票
     */
    TicketingResponse issueTicket(TicketingRequest request);
    
    /**
     * 查询出票状态
     */
    TicketingResponse queryTicketingStatus(String orderNumber);
    
    /**
     * 取消出票
     */
    TicketingResponse cancelTicket(String ticketNumber, String reason);
    
    /**
     * 改签
     */
    TicketingResponse changeTicket(String ticketNumber, TicketingRequest newRequest);
    
    /**
     * 查询座位可用性
     */
    boolean checkSeatAvailability(String flightNumber, String seatClass);
} 