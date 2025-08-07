package com.airline.order.repository;

import com.airline.order.entity.SeatDetail;
import com.airline.order.entity.SeatDetail.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 座位详情数据访问接口
 */
@Repository
public interface SeatDetailRepository extends JpaRepository<SeatDetail, Long> {
    
    /**
     * 根据航班ID查询座位详情
     * @param flightId 航班ID
     * @return 座位详情列表
     */
    List<SeatDetail> findByFlightInfoId(Long flightId);
    
    /**
     * 根据航班ID和座位状态查询座位详情
     * @param flightId 航班ID
     * @param status 座位状态
     * @return 座位详情列表
     */
    List<SeatDetail> findByFlightInfoIdAndSeatStatus(Long flightId, SeatStatus status);
    
    /**
     * 根据航班ID删除座位详情
     * @param flightId 航班ID
     */
    void deleteByFlightInfoId(Long flightId);
    
    /**
     * 根据航班ID和座位号查询座位详情
     * @param flightId 航班ID
     * @param seatNumber 座位号
     * @return 座位详情
     */
    SeatDetail findByFlightInfoIdAndSeatNumber(Long flightId, String seatNumber);
}