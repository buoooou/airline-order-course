package com.postion.airlineorderbackend.entity;

import com.postion.airlineorderbackend.enums.FlightStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 航班信息实体类
 * 对应数据库表 flight_info_qiaozhe
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Entity
@Table(name = "flight_info_qiaozhe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightInfo {
    
    /**
     * 航班信息ID - 主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 航班号 - 不能为空，如 CA1234
     */
    @Column(name = "flight_number", nullable = false, length = 20)
    private String flightNumber;
    
    /**
     * 航空公司名称 - 不能为空
     */
    @Column(name = "airline", nullable = false, length = 100)
    private String airline;
    
    /**
     * 出发机场代码 - 不能为空，如 PEK
     */
    @Column(name = "departure_airport", nullable = false, length = 10)
    private String departureAirport;
    
    /**
     * 到达机场代码 - 不能为空，如 SHA
     */
    @Column(name = "arrival_airport", nullable = false, length = 10)
    private String arrivalAirport;
    
    /**
     * 出发时间 - 不能为空
     */
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    
    /**
     * 到达时间 - 不能为空
     */
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    /**
     * 机型 - 可以为空，如 Boeing 737
     */
    @Column(name = "aircraft_type", length = 50)
    private String aircraftType;
    
    /**
     * 票价 - 不能为空，精度为10位，小数点后2位
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * 可用座位数 - 不能为空，默认为0
     */
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats = 0;
    
    /**
     * 总座位数 - 不能为空，默认为0
     */
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats = 0;
    
    /**
     * 航班状态 - 枚举类型，不能为空，默认为ACTIVE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FlightStatus status = FlightStatus.ACTIVE;
    
    /**
     * 创建时间 - 自动设置
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间 - 自动更新
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 关联的订单 - 一对一关系
     * 一个航班信息对应一个订单
     */
    @OneToOne(mappedBy = "flightInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Order order;
    
    /**
     * 在持久化之前设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    /**
     * 在更新之前设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查航班是否可以预订
     * @return 是否可以预订
     */
    public boolean isBookable() {
        return this.status != null && this.status.isBookable() && this.availableSeats > 0;
    }
    
    /**
     * 检查航班是否已满座
     * @return 是否已满座
     */
    public boolean isFullyBooked() {
        return this.availableSeats <= 0;
    }
    
    /**
     * 检查是否有足够的可用座位
     * @param seatCount 需要的座位数
     * @return 是否有足够的座位
     */
    public boolean hasAvailableSeats(int seatCount) {
        return this.availableSeats >= seatCount && isBookable();
    }
    
    /**
     * 预订座位 - 减少可用座位数
     * @param seatCount 预订的座位数
     * @return 是否预订成功
     */
    public boolean bookSeats(int seatCount) {
        if (this.availableSeats >= seatCount && isBookable()) {
            this.availableSeats -= seatCount;
            return true;
        }
        return false;
    }
    
    /**
     * 释放座位 - 增加可用座位数（取消订单时使用）
     * @param seatCount 释放的座位数
     */
    public void releaseSeats(int seatCount) {
        this.availableSeats = Math.min(this.availableSeats + seatCount, this.totalSeats);
    }
    
    /**
     * 获取座位占用率
     * @return 座位占用率（0-1之间的小数）
     */
    public double getOccupancyRate() {
        if (this.totalSeats == 0) {
            return 0.0;
        }
        return (double) (this.totalSeats - this.availableSeats) / this.totalSeats;
    }
    
    /**
     * 计算飞行时长（分钟）
     * @return 飞行时长（分钟）
     */
    public long getFlightDurationMinutes() {
        if (this.departureTime != null && this.arrivalTime != null) {
            return java.time.Duration.between(this.departureTime, this.arrivalTime).toMinutes();
        }
        return 0;
    }
}
