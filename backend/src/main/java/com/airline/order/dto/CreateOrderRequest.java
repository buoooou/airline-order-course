package com.airline.order.dto;

import java.math.BigDecimal;

/**
 * 创建订单请求DTO
 */
public class CreateOrderRequest {
    
    private Long userId;
    private Long flightId;
    private String seatNumber;
    private BigDecimal amount;
    
    // 构造函数
    public CreateOrderRequest() {
    }
    
    public CreateOrderRequest(Long userId, Long flightId, String seatNumber, BigDecimal amount) {
        this.userId = userId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.amount = amount;
    }
    
    // Getter和Setter方法
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getFlightId() {
        return flightId;
    }
    
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "userId=" + userId +
                ", flightId=" + flightId +
                ", seatNumber='" + seatNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}