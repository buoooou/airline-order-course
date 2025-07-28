package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.enums.FlightStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 航班信息数据传输对象
 * 用于前后端数据交互
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightInfoDTO {
    
    /**
     * 航班信息ID
     */
    private Long id;
    
    /**
     * 航班号
     */
    private String flightNumber;
    
    /**
     * 航空公司名称
     */
    private String airline;
    
    /**
     * 出发机场代码
     */
    private String departureAirport;
    
    /**
     * 到达机场代码
     */
    private String arrivalAirport;
    
    /**
     * 出发时间
     */
    private LocalDateTime departureTime;
    
    /**
     * 到达时间
     */
    private LocalDateTime arrivalTime;
    
    /**
     * 机型
     */
    private String aircraftType;
    
    /**
     * 票价
     */
    private BigDecimal price;
    
    /**
     * 可用座位数
     */
    private Integer availableSeats;
    
    /**
     * 总座位数
     */
    private Integer totalSeats;
    
    /**
     * 航班状态
     */
    private FlightStatus status;
    
    /**
     * 航班状态描述
     */
    private String statusDescription;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 飞行时长（分钟）
     */
    private Long flightDurationMinutes;
    
    /**
     * 座位占用率
     */
    private Double occupancyRate;
    
    /**
     * 是否可预订
     */
    private Boolean bookable;
    
    /**
     * 是否已满座
     */
    private Boolean fullyBooked;
    
    /**
     * 构造函数 - 基本信息
     */
    public FlightInfoDTO(Long id, String flightNumber, String airline, 
                        String departureAirport, String arrivalAirport,
                        LocalDateTime departureTime, LocalDateTime arrivalTime,
                        BigDecimal price, Integer availableSeats, Integer totalSeats,
                        FlightStatus status) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.status = status;
        this.statusDescription = status != null ? status.getDescription() : "";
        
        // 计算衍生字段
        this.calculateDerivedFields();
    }
    
    /**
     * 计算衍生字段
     */
    private void calculateDerivedFields() {
        // 计算飞行时长
        if (this.departureTime != null && this.arrivalTime != null) {
            this.flightDurationMinutes = java.time.Duration.between(this.departureTime, this.arrivalTime).toMinutes();
        }
        
        // 计算座位占用率
        if (this.totalSeats != null && this.totalSeats > 0 && this.availableSeats != null) {
            this.occupancyRate = (double) (this.totalSeats - this.availableSeats) / this.totalSeats;
        } else {
            this.occupancyRate = 0.0;
        }
        
        // 判断是否可预订
        this.bookable = this.status != null && this.status.isBookable() && 
                       this.availableSeats != null && this.availableSeats > 0;
        
        // 判断是否已满座
        this.fullyBooked = this.availableSeats != null && this.availableSeats <= 0;
    }
    
    /**
     * 获取航线描述
     * @return 航线描述，如 "PEK → PVG"
     */
    public String getRouteDescription() {
        return this.departureAirport + " → " + this.arrivalAirport;
    }
    
    /**
     * 获取时间段描述
     * @return 时间段描述，如 "08:00 - 10:30"
     */
    public String getTimeRangeDescription() {
        if (this.departureTime != null && this.arrivalTime != null) {
            return this.departureTime.toLocalTime() + " - " + this.arrivalTime.toLocalTime();
        }
        return "";
    }
    
    /**
     * 获取飞行时长描述
     * @return 飞行时长描述，如 "2小时30分钟"
     */
    public String getFlightDurationDescription() {
        if (this.flightDurationMinutes != null && this.flightDurationMinutes > 0) {
            long hours = this.flightDurationMinutes / 60;
            long minutes = this.flightDurationMinutes % 60;
            
            if (hours > 0 && minutes > 0) {
                return hours + "小时" + minutes + "分钟";
            } else if (hours > 0) {
                return hours + "小时";
            } else {
                return minutes + "分钟";
            }
        }
        return "";
    }
    
    /**
     * 获取座位信息描述
     * @return 座位信息描述，如 "剩余150/180座位"
     */
    public String getSeatInfoDescription() {
        if (this.availableSeats != null && this.totalSeats != null) {
            return "剩余" + this.availableSeats + "/" + this.totalSeats + "座位";
        }
        return "";
    }
}
