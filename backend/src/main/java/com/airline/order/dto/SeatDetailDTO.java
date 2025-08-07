package com.airline.order.dto;

import com.airline.order.entity.SeatDetail.SeatStatus;
import com.airline.order.entity.SeatDetail.SeatType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 座位详情数据传输对象
 */
public class SeatDetailDTO {
    
    private Long id;
    private Long flightId;
    private String flightNumber; // 冗余字段，方便前端展示
    private String seatNumber;
    private SeatType seatType;
    private SeatStatus seatStatus;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public SeatDetailDTO() {
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getFlightId() {
        return flightId;
    }
    
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public SeatType getSeatType() {
        return seatType;
    }
    
    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }
    
    public SeatStatus getSeatStatus() {
        return seatStatus;
    }
    
    public void setSeatStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "SeatDetailDTO{" +
                "id=" + id +
                ", flightId=" + flightId +
                ", flightNumber='" + flightNumber + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", seatType=" + seatType +
                ", seatStatus=" + seatStatus +
                ", price=" + price +
                '}';
    }
}