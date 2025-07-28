package com.postion.airlineorderbackend.repository;

import com.postion.airlineorderbackend.entity.FlightInfo;
import com.postion.airlineorderbackend.enums.FlightStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 航班信息数据访问接口
 * 提供航班信息相关的数据库操作方法
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Repository
public interface FlightInfoRepository extends JpaRepository<FlightInfo, Long> {
    
    /**
     * 根据航班号查找航班信息
     * 用于航班号唯一性检查和查询
     * 
     * @param flightNumber 航班号
     * @return 航班信息列表
     */
    List<FlightInfo> findByFlightNumber(String flightNumber);
    
    /**
     * 根据航班号和出发时间查找航班（唯一约束）
     * 用于精确查找特定航班
     * 
     * @param flightNumber 航班号
     * @param departureTime 出发时间
     * @return 航班信息（可能为空）
     */
    Optional<FlightInfo> findByFlightNumberAndDepartureTime(String flightNumber, LocalDateTime departureTime);
    
    /**
     * 根据出发机场和到达机场查找航班
     * 用于航线查询
     * 
     * @param departureAirport 出发机场代码
     * @param arrivalAirport 到达机场代码
     * @return 航班信息列表
     */
    List<FlightInfo> findByDepartureAirportAndArrivalAirport(String departureAirport, String arrivalAirport);
    
    /**
     * 根据航空公司查找航班
     * 用于按航空公司筛选
     * 
     * @param airline 航空公司名称
     * @return 航班信息列表
     */
    List<FlightInfo> findByAirline(String airline);
    
    /**
     * 根据航班状态查找航班
     * 用于按状态筛选航班
     * 
     * @param status 航班状态
     * @return 航班信息列表
     */
    List<FlightInfo> findByStatus(FlightStatus status);
    
    /**
     * 查找可预订的航班（状态为ACTIVE且有可用座位）
     * 用于航班搜索功能
     * 
     * @return 可预订的航班列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.status = 'ACTIVE' AND f.availableSeats > 0")
    List<FlightInfo> findBookableFlights();
    
    /**
     * 根据出发时间范围查找航班
     * 用于按时间段筛选航班
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 航班信息列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.departureTime BETWEEN :startTime AND :endTime")
    List<FlightInfo> findByDepartureTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 综合搜索航班（出发地、目的地、时间范围）
     * 用于航班搜索功能
     * 
     * @param departureAirport 出发机场代码
     * @param arrivalAirport 到达机场代码
     * @param startTime 出发时间范围开始
     * @param endTime 出发时间范围结束
     * @param pageable 分页参数
     * @return 分页的航班信息
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.departureAirport = :departureAirport " +
           "AND f.arrivalAirport = :arrivalAirport " +
           "AND f.departureTime BETWEEN :startTime AND :endTime " +
           "AND f.status = 'ACTIVE' " +
           "ORDER BY f.departureTime ASC")
    Page<FlightInfo> searchFlights(@Param("departureAirport") String departureAirport,
                                  @Param("arrivalAirport") String arrivalAirport,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  Pageable pageable);
    
    /**
     * 根据价格范围查找航班
     * 用于价格筛选功能
     * 
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 航班信息列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.price BETWEEN :minPrice AND :maxPrice")
    List<FlightInfo> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, 
                                       @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * 查找即将起飞的航班（未来2小时内）
     * 用于航班提醒功能
     * 
     * @return 即将起飞的航班列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.departureTime BETWEEN :now AND :twoHoursLater " +
           "AND f.status = 'ACTIVE'")
    List<FlightInfo> findUpcomingFlights(@Param("now") LocalDateTime now, 
                                        @Param("twoHoursLater") LocalDateTime twoHoursLater);
    
    /**
     * 查找座位紧张的航班（可用座位少于总座位的20%）
     * 用于座位预警功能
     * 
     * @return 座位紧张的航班列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.availableSeats < (f.totalSeats * 0.2) " +
           "AND f.status = 'ACTIVE' AND f.availableSeats > 0")
    List<FlightInfo> findLowAvailabilityFlights();
    
    /**
     * 根据机型查找航班
     * 用于机型筛选功能
     * 
     * @param aircraftType 机型
     * @return 航班信息列表
     */
    List<FlightInfo> findByAircraftType(String aircraftType);
    
    /**
     * 查找热门航线（按订单数量排序）
     * 用于数据分析功能
     * 
     * @param limit 返回数量限制
     * @return 热门航线的航班信息
     */
    @Query("SELECT f FROM FlightInfo f JOIN f.order o " +
           "GROUP BY f.departureAirport, f.arrivalAirport " +
           "ORDER BY COUNT(o) DESC")
    List<FlightInfo> findPopularRoutes(@Param("limit") int limit);
    
    /**
     * 统计指定日期的航班数量
     * 用于统计分析
     * 
     * @param date 指定日期
     * @return 航班数量
     */
    @Query("SELECT COUNT(f) FROM FlightInfo f WHERE DATE(f.departureTime) = DATE(:date)")
    long countFlightsByDate(@Param("date") LocalDateTime date);
    
    /**
     * 查找指定航空公司在指定时间范围内的航班
     * 用于航空公司数据分析
     * 
     * @param airline 航空公司
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 航班信息列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.airline = :airline " +
           "AND f.departureTime BETWEEN :startTime AND :endTime")
    List<FlightInfo> findByAirlineAndTimeRange(@Param("airline") String airline,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找已满座的航班
     * 用于座位管理
     * 
     * @return 已满座的航班列表
     */
    @Query("SELECT f FROM FlightInfo f WHERE f.availableSeats = 0 AND f.status = 'ACTIVE'")
    List<FlightInfo> findFullyBookedFlights();
    
    /**
     * 根据多个条件搜索航班（支持模糊查询）
     * 用于高级搜索功能
     * 
     * @param flightNumber 航班号（可为空）
     * @param airline 航空公司（可为空）
     * @param departureAirport 出发机场（可为空）
     * @param arrivalAirport 到达机场（可为空）
     * @param status 航班状态（可为空）
     * @param pageable 分页参数
     * @return 分页的航班信息
     */
    @Query("SELECT f FROM FlightInfo f WHERE " +
           "(:flightNumber IS NULL OR f.flightNumber LIKE %:flightNumber%) AND " +
           "(:airline IS NULL OR f.airline LIKE %:airline%) AND " +
           "(:departureAirport IS NULL OR f.departureAirport = :departureAirport) AND " +
           "(:arrivalAirport IS NULL OR f.arrivalAirport = :arrivalAirport) AND " +
           "(:status IS NULL OR f.status = :status)")
    Page<FlightInfo> findByMultipleConditions(@Param("flightNumber") String flightNumber,
                                             @Param("airline") String airline,
                                             @Param("departureAirport") String departureAirport,
                                             @Param("arrivalAirport") String arrivalAirport,
                                             @Param("status") FlightStatus status,
                                             Pageable pageable);
}
