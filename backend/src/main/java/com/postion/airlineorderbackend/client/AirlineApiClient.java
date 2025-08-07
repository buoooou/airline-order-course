package com.postion.airlineorderbackend.client;

import com.postion.airlineorderbackend.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class AirlineApiClient {
    private static final Logger log = LoggerFactory.getLogger(AirlineApiClient.class);

    /**
     * issueTicket
     * 
     * @param orderId 订单ID
     * @return ticket
     * @throws InterruptedException
     * @throws RuntimeException
     */
    public String issueTicket(Long orderId) throws IllegalArgumentException {

        log.info("Start issueTicket(), orderId:{}", orderId);

        try {
            // 模拟网络延迟和处理时间 (2-5秒)
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));

            // 模拟接口成功率 (80%成功, 20%失败)
            if (ThreadLocalRandom.current().nextInt(10) < 8) {
                log.info("End issueTicket(), orderId:{} Ticket issuance successful ", orderId);
                return "TKT" + System.currentTimeMillis();
            } else {
                log.info("End issueTicket(), orderId:{} Ticket issuance failure ", orderId);
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "Airline API error: Insufficient seats");
            }
        } catch (InterruptedException e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }
    }
}