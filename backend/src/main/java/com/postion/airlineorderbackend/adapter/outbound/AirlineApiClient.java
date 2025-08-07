package com.postion.airlineorderbackend.adapter.outbound;

import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.Random;

@Service
public class AirlineApiClient {

    private final Random random = new Random();

    /**
     * 模拟调用航空公司API出票
     * 
     * @param orderId 订单ID
     * @return 票号
     * @throws InterruptedException 如果线程被中断
     * @throws RuntimeException 如果API调用失败
     */
    public String issueTicket(Long orderId) throws InterruptedException {
        System.out.println("开始为订单 " + orderId + " 调用航空公司出票接口...");
        
        // 模拟网络延迟和处理时间 (2-5秒)
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 5));
        
        // 模拟成功率 (85%成功，15%失败)
        if (random.nextInt(100) < 85) {
            String ticketNumber = "TK" + System.currentTimeMillis() + "-" + orderId;
            System.out.println("订单 " + orderId + " 出票成功，票号: " + ticketNumber);
            return ticketNumber;
        } else {
            System.out.println("订单 " + orderId + " 出票失败，航空公司系统异常");
            throw new RuntimeException("航空公司出票系统暂时不可用，请稍后重试");
        }
    }
    
    /**
     * 模拟查询票务状态
     * 
     * @param ticketNumber 票号
     * @return 票务状态 ("VALID", "INVALID", "REFUNDED")
     * @throws InterruptedException 如果线程被中断
     */
    public String queryTicketStatus(String ticketNumber) throws InterruptedException {
        System.out.println("查询票号 " + ticketNumber + " 的状态...");
        
        // 模拟网络延迟 (1-3秒)
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 3));
        
        // 模拟不同的票务状态
        String[] statuses = {"VALID", "VALID", "VALID", "VALID", "INVALID", "REFUNDED"};
        String status = statuses[random.nextInt(statuses.length)];
        
        System.out.println("票号 " + ticketNumber + " 的状态为: " + status);
        return status;
    }
    
    /**
     * 模拟退票操作
     * 
     * @param ticketNumber 票号
     * @return 是否退票成功
     * @throws InterruptedException 如果线程被中断
     */
    public boolean refundTicket(String ticketNumber) throws InterruptedException {
        System.out.println("开始为票号 " + ticketNumber + " 申请退票...");
        
        // 模拟网络延迟 (2-4秒)
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 4));
        
        // 模拟退票成功率 (90%成功，10%失败)
        boolean success = random.nextInt(100) < 90;
        
        if (success) {
            System.out.println("票号 " + ticketNumber + " 退票成功");
        } else {
            System.out.println("票号 " + ticketNumber + " 退票失败，可能已过退票时限");
        }
        
        return success;
    }
}