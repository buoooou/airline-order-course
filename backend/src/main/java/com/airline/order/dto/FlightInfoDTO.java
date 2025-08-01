package com.airline.order.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 航班信息数据传输对象
 */
public class FlightInfoDTO {
    
    private Long id;
    private String flightNumber;
    private String departureAirportCode;
    private String departureAirportName;
    private String arrivalAirportCode;
    private String arrivalAirportName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer flightDuration;
    private List<OrderDTO> orders;
    
    // 构造函数
    public FlightInfoDTO() {
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getDepartureAirportCode() {
        return departureAirportCode;
    }
    
    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
    }
    
    public String getDepartureAirportName() {
        return departureAirportName;
    }
    
    public void setDepartureAirportName(String departureAirportName) {
        this.departureAirportName = departureAirportName;
    }
    
    public String getArrivalAirportCode() {
        return arrivalAirportCode;
    }
    
    public void setArrivalAirportCode(String arrivalAirportCode) {
        this.arrivalAirportCode = arrivalAirportCode;
    }
    
    public String getArrivalAirportName() {
        return arrivalAirportName;
    }
    
    public void setArrivalAirportName(String arrivalAirportName) {
        this.arrivalAirportName = arrivalAirportName;
    }
    
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public Integer getFlightDuration() {
        return flightDuration;
    }
    
    public void setFlightDuration(Integer flightDuration) {
        this.flightDuration = flightDuration;
    }
    
    public List<OrderDTO> getOrders() {
        return orders;
    }
    
    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }
    
    @Override
    public String toString() {
        return "FlightInfoDTO{" +
                "id=" + id +
                ", flightNumber='" + flightNumber + '\'' +
                ", departureAirportCode='" + departureAirportCode + '\'' +
                ", arrivalAirportCode='" + arrivalAirportCode + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}