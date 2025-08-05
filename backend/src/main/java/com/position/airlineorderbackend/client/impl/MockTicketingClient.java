package com.position.airlineorderbackend.client.impl;

import com.position.airlineorderbackend.client.TicketingClient;
import com.position.airlineorderbackend.dto.TicketingRequest;
import com.position.airlineorderbackend.dto.TicketingResponse;
import com.position.airlineorderbackend.exception.TicketingSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Mock出票系统客户端实现
 * 模拟各种异常场景和网络问题
 */
@Service
public class MockTicketingClient implements TicketingClient {

    private static final Logger logger = LoggerFactory.getLogger(MockTicketingClient.class);
    private final Random random = new Random();

    @Override
    public TicketingResponse issueTicket(TicketingRequest request) {
        logger.info("Mock出票系统 - 开始出票，订单号: {}", request.getOrderNumber());
        
        // 模拟网络延迟
        simulateNetworkDelay();
        
        // 模拟各种异常场景
        String orderNumber = request.getOrderNumber();
        
        // 1. 模拟系统超时
        if (orderNumber.contains("TIMEOUT")) {
            logger.error("Mock出票系统 - 模拟系统超时");
            throw new TicketingSystemException("TICKETING_SYSTEM_TIMEOUT", "出票系统超时，请稍后重试");
        }
        
        // 2. 模拟座位不足
        if (orderNumber.contains("NO_SEAT")) {
            logger.error("Mock出票系统 - 座位不足");
            TicketingResponse response = new TicketingResponse();
            response.setOrderNumber(orderNumber);
            response.setStatus("FAILED");
            response.setErrorCode("NO_SEAT_AVAILABLE");
            response.setErrorMessage("所选座位类型已售罄");
            response.setTicketingTime(LocalDateTime.now());
            return response;
        }
        
        // 3. 模拟航班取消
        if (orderNumber.contains("FLIGHT_CANCELLED")) {
            logger.error("Mock出票系统 - 航班已取消");
            TicketingResponse response = new TicketingResponse();
            response.setOrderNumber(orderNumber);
            response.setStatus("FAILED");
            response.setErrorCode("FLIGHT_CANCELLED");
            response.setErrorMessage("航班已取消，无法出票");
            response.setTicketingTime(LocalDateTime.now());
            return response;
        }
        
        // 4. 模拟乘客信息错误
        if (orderNumber.contains("INVALID_PASSENGER")) {
            logger.error("Mock出票系统 - 乘客信息错误");
            TicketingResponse response = new TicketingResponse();
            response.setOrderNumber(orderNumber);
            response.setStatus("FAILED");
            response.setErrorCode("INVALID_PASSENGER_INFO");
            response.setErrorMessage("乘客身份证号格式错误");
            response.setTicketingTime(LocalDateTime.now());
            return response;
        }
        
        // 5. 模拟系统维护
        if (orderNumber.contains("MAINTENANCE")) {
            logger.error("Mock出票系统 - 系统维护中");
            throw new TicketingSystemException("SYSTEM_MAINTENANCE", "出票系统正在维护，请稍后重试");
        }
        
        // 6. 模拟随机失败（10%概率）
        if (random.nextInt(100) < 10) {
            logger.error("Mock出票系统 - 随机失败");
            throw new TicketingSystemException("RANDOM_ERROR", "出票系统临时错误，请重试");
        }
        
        // 7. 模拟网络连接失败
        if (orderNumber.contains("NETWORK_ERROR")) {
            logger.error("Mock出票系统 - 网络连接失败");
            throw new TicketingSystemException("NETWORK_ERROR", "无法连接到出票系统");
        }
        
        // 正常出票流程
        logger.info("Mock出票系统 - 出票成功");
        TicketingResponse response = new TicketingResponse();
        response.setTicketNumber("TKT" + System.currentTimeMillis());
        response.setOrderNumber(orderNumber);
        response.setStatus("SUCCESS");
        response.setMessage("出票成功");
        response.setTicketingTime(LocalDateTime.now());
        response.setSeatNumber("A" + (random.nextInt(30) + 1));
        response.setGateNumber("G" + (random.nextInt(20) + 1));
        
        return response;
    }

    @Override
    public TicketingResponse queryTicketingStatus(String orderNumber) {
        logger.info("Mock出票系统 - 查询出票状态，订单号: {}", orderNumber);
        
        simulateNetworkDelay();
        
        // 模拟查询失败
        if (orderNumber.contains("QUERY_FAILED")) {
            throw new TicketingSystemException("QUERY_FAILED", "查询出票状态失败");
        }
        
        TicketingResponse response = new TicketingResponse();
        response.setOrderNumber(orderNumber);
        response.setStatus("SUCCESS");
        response.setMessage("查询成功");
        response.setTicketingTime(LocalDateTime.now());
        
        return response;
    }

    @Override
    public TicketingResponse cancelTicket(String ticketNumber, String reason) {
        logger.info("Mock出票系统 - 取消出票，票号: {}, 原因: {}", ticketNumber, reason);
        
        simulateNetworkDelay();
        
        // 模拟取消失败
        if (ticketNumber.contains("CANCEL_FAILED")) {
            throw new TicketingSystemException("CANCEL_FAILED", "取消出票失败");
        }
        
        TicketingResponse response = new TicketingResponse();
        response.setTicketNumber(ticketNumber);
        response.setStatus("SUCCESS");
        response.setMessage("取消出票成功");
        response.setTicketingTime(LocalDateTime.now());
        
        return response;
    }

    @Override
    public TicketingResponse changeTicket(String ticketNumber, TicketingRequest newRequest) {
        logger.info("Mock出票系统 - 改签，票号: {}", ticketNumber);
        
        simulateNetworkDelay();
        
        // 模拟改签失败
        if (ticketNumber.contains("CHANGE_FAILED")) {
            throw new TicketingSystemException("CHANGE_FAILED", "改签失败");
        }
        
        TicketingResponse response = new TicketingResponse();
        response.setTicketNumber("TKT" + System.currentTimeMillis() + "_CHANGED");
        response.setOrderNumber(newRequest.getOrderNumber());
        response.setStatus("SUCCESS");
        response.setMessage("改签成功");
        response.setTicketingTime(LocalDateTime.now());
        
        return response;
    }

    @Override
    public boolean checkSeatAvailability(String flightNumber, String seatClass) {
        logger.info("Mock出票系统 - 查询座位可用性，航班: {}, 座位类型: {}", flightNumber, seatClass);
        
        simulateNetworkDelay();
        
        // 模拟座位查询失败
        if (flightNumber.contains("SEAT_QUERY_FAILED")) {
            throw new TicketingSystemException("SEAT_QUERY_FAILED", "座位查询失败");
        }
        
        // 模拟座位不足
        if (flightNumber.contains("NO_SEAT")) {
            return false;
        }
        
        // 随机返回座位可用性
        return random.nextBoolean();
    }
    
    /**
     * 模拟网络延迟
     */
    private void simulateNetworkDelay() {
        try {
            // 模拟100-500ms的网络延迟
            TimeUnit.MILLISECONDS.sleep(100 + random.nextInt(400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 