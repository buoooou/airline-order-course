package com.postion.airlineorderbackend.adapter;

import com.postion.airlineorderbackend.exception.AirlineApiException;

public interface AirlineApi {
    /**
     * 为指定订单ID出票。
     * @param orderId 订单ID
     * @return 出票成功的票号
     * @throws AirlineApiException 如果出票失败
     * @throws InterruptedException 如果线程被中断
     */
    String issueTicket(Long orderId) throws InterruptedException, AirlineApiException;
}