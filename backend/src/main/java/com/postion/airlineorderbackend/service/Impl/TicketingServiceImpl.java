package com.postion.airlineorderbackend.service.Impl;

import com.postion.airlineorderbackend.adapter.outbound.AirlineAppClient;
import com.postion.airlineorderbackend.service.TicketingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketingServiceImpl implements TicketingService {

    private final AirlineAppClient airlineAppClient;

    @Override
    public void issueTicket(String orderId) {
        log.info("开始出票流程，订单号：{}", orderId);
        airlineAppClient.issueTicket(orderId);
    }
}
