package com.airline.order.repository;

import com.airline.order.entity.FlightInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 航班信息数据访问层接口
 */
@Repository
public interface FlightInfoRepository extends JpaRepository<FlightInfo, Long> {
    
    /**
     * 根据航班号查找航班
     * @param flightNumber 航班号
     * @return 航班信息
     */
    Optional<FlightInfo> findByFlightNumber(String flightNumber);
    
    /**
     * 根据出发机场代码查找航班列表
     * @param departureAirportCode 出发机场代码
     * @return 航班列表
     */
    List<FlightInfo> findByDepartureAirportCode(String departureAirportCode);
    
    /**
     * 根据到达机场代码查找航班列表
     * @param arrivalAirportCode 到达机场代码
     * @return 航班列表
     */
    List<FlightInfo> findByArrivalAirportCode(String arrivalAirportCode);
    
    /**
     * 根据出发和到达机场代码查找航班
     * @param departureAirportCode 出发机场代码
     * @param arrivalAirportCode 到达机场代码
     * @return 航班列表
     */
    List<FlightInfo> findByDepartureAirportCodeAndArrivalAirportCode(
            String departureAirportCode, String arrivalAirportCode);
    
    /**
     * 根据出发时间范围查找航班
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 航班列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.departureTime BETWEEN :startTime AND :endTime")
    List<FlightInfo> findByDepartureTimeBetween(
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据航班号模糊查询
     * @param flightNumber 航班号关键字
     * @return 航班列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.flightNumber LIKE %:flightNumber%")
    List<FlightInfo> findByFlightNumberContaining(@Param("flightNumber") String flightNumber);
    
    /**
     * 查找指定日期的航班
     * @param date 日期
     * @return 航班列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE DATE(f.departureTime) = DATE(:date)")
    List<FlightInfo> findByDepartureDate(@Param("date") LocalDateTime date);
    
    /**
     * 根据出发机场名称查找航班
     * @param departureAirportName 出发机场名称
     * @return 航班列表
     */
    List<FlightInfo> findByDepartureAirportName(String departureAirportName);
    
    /**
     * 根据到达机场名称查找航班
     * @param arrivalAirportName 到达机场名称
     * @return 航班列表
     */
    List<FlightInfo> findByArrivalAirportName(String arrivalAirportName);
    
    /**
     * 查找有订单的航班
     * @return 航班列表
     */
    @Query("SELECT DISTINCT f FROM FlightInfo f JOIN f.orders o")
    List<FlightInfo> findFlightsWithOrders();
    
    /**
     * 根据飞行时长范围查找航班
     * @param minDuration 最小飞行时长（分钟）
     * @param maxDuration 最大飞行时长（分钟）
     * @return 航班列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.flightDuration BETWEEN :minDuration AND :maxDuration")
    List<FlightInfo> findByFlightDurationBetween(
            @Param("minDuration") Integer minDuration, 
            @Param("maxDuration") Integer maxDuration);
}