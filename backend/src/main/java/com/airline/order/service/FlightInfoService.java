package com.airline.order.service;

import com.airline.order.dto.FlightInfoDTO;
import com.airline.order.entity.FlightInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 航班信息服务接口
 */
public interface FlightInfoService {
    
    /**
     * 获取所有航班信息
     * @return 航班信息列表
     */
    List<FlightInfoDTO> getAllFlights();
    
    /**
     * 根据ID获取航班信息
     * @param id 航班ID
     * @return 航班信息
     */
    FlightInfoDTO getFlightById(Long id);
    
    /**
     * 根据航班号获取航班信息
     * @param flightNumber 航班号
     * @return 航班信息
     */
    FlightInfoDTO getFlightByNumber(String flightNumber);
    
    /**
     * 创建新航班
     * @param flightInfoDTO 航班信息
     * @return 创建的航班信息
     */
    FlightInfoDTO createFlight(FlightInfoDTO flightInfoDTO);
    
    /**
     * 更新航班信息
     * @param id 航班ID
     * @param flightInfoDTO 航班信息
     * @return 更新后的航班信息
     */
    FlightInfoDTO updateFlight(Long id, FlightInfoDTO flightInfoDTO);
    
    /**
     * 删除航班
     * @param id 航班ID
     */
    void deleteFlight(Long id);
    
    /**
     * 根据出发和到达机场代码查询航班
     * @param departureCode 出发机场代码
     * @param arrivalCode 到达机场代码
     * @return 航班信息列表
     */
    List<FlightInfoDTO> searchFlightsByRoute(String departureCode, String arrivalCode);
    
    /**
     * 根据出发日期查询航班
     * @param departureDate 出发日期
     * @return 航班信息列表
     */
    List<FlightInfoDTO> searchFlightsByDate(LocalDateTime departureDate);
    
    /**
     * 根据航班号关键字模糊查询
     * @param keyword 航班号关键字
     * @return 航班信息列表
     */
    List<FlightInfoDTO> searchFlightsByNumberKeyword(String keyword);
    
    /**
     * 根据飞行时长范围查询航班
     * @param minDuration 最小飞行时长（分钟）
     * @param maxDuration 最大飞行时长（分钟）
     * @return 航班信息列表
     */
    List<FlightInfoDTO> searchFlightsByDuration(Integer minDuration, Integer maxDuration);
    
    /**
     * 将实体对象转换为DTO
     * @param flightInfo 航班实体
     * @return 航班DTO
     */
    FlightInfoDTO convertToDTO(FlightInfo flightInfo);
    
    /**
     * 将DTO转换为实体对象
     * @param flightInfoDTO 航班DTO
     * @return 航班实体
     */
    FlightInfo convertToEntity(FlightInfoDTO flightInfoDTO);
}