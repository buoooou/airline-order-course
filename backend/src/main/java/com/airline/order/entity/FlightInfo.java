package com.airline.order.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 航班信息实体类
 */
@Entity
@Table(name = "flight_info")
public class FlightInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "flight_number")
    private String flightNumber;
    
    @Column(name = "departure_airport_code")
    private String departureAirportCode;
    
    @Column(name = "departure_airport_name")
    private String departureAirportName;
    
    @Column(name = "arrival_airport_code")
    private String arrivalAirportCode;
    
    @Column(name = "arrival_airport_name")
    private String arrivalAirportName;
    
    @Column(name = "departure_time")
    private LocalDateTime departureTime;
    
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;
    
    @Column(name = "flight_duration")
    private Integer flightDuration;
    
    @OneToMany(mappedBy = "flightInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
    
    // 构造函数
    public FlightInfo() {
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
    
    public List<Order> getOrders() {
        return orders;
    }
    
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    @Override
    public String toString() {
        return "FlightInfo{" +
                "id=" + id +
                ", flightNumber='" + flightNumber + '\'' +
                ", departureAirportCode='" + departureAirportCode + '\'' +
                ", arrivalAirportCode='" + arrivalAirportCode + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}