package com.airline.order.service;

import com.airline.order.dto.SeatDetailDTO;
import com.airline.order.entity.SeatDetail;
import com.airline.order.entity.SeatDetail.SeatStatus;

import java.util.List;

/**
 * 座位详情服务接口
 */
public interface SeatDetailService {
    
    /**
     * 获取所有座位详情
     * @return 座位详情列表
     */
    List<SeatDetailDTO> getAllSeatDetails();
    
    /**
     * 根据ID获取座位详情
     * @param id 座位ID
     * @return 座位详情
     */
    SeatDetailDTO getSeatDetailById(Long id);
    
    /**
     * 根据航班ID获取所有座位详情
     * @param flightId 航班ID
     * @return 座位详情列表
     */
    List<SeatDetailDTO> getSeatDetailsByFlightId(Long flightId);
    
    /**
     * 根据航班ID和座位状态获取座位详情
     * @param flightId 航班ID
     * @param status 座位状态
     * @return 座位详情列表
     */
    List<SeatDetailDTO> getSeatDetailsByFlightIdAndStatus(Long flightId, SeatStatus status);
    
    /**
     * 创建新座位详情
     * @param seatDetailDTO 座位详情
     * @return 创建的座位详情
     */
    SeatDetailDTO createSeatDetail(SeatDetailDTO seatDetailDTO);
    
    /**
     * 批量创建座位详情
     * @param seatDetailDTOs 座位详情列表
     * @return 创建的座位详情列表
     */
    List<SeatDetailDTO> createSeatDetails(List<SeatDetailDTO> seatDetailDTOs);
    
    /**
     * 更新座位详情
     * @param id 座位ID
     * @param seatDetailDTO 座位详情
     * @return 更新后的座位详情
     */
    SeatDetailDTO updateSeatDetail(Long id, SeatDetailDTO seatDetailDTO);
    
    /**
     * 更新座位状态
     * @param id 座位ID
     * @param status 新状态
     * @return 更新后的座位详情
     */
    SeatDetailDTO updateSeatStatus(Long id, SeatStatus status);
    
    /**
     * 删除座位详情
     * @param id 座位ID
     */
    void deleteSeatDetail(Long id);
    
    /**
     * 根据航班ID删除所有座位详情
     * @param flightId 航班ID
     */
    void deleteSeatDetailsByFlightId(Long flightId);
    
    /**
     * 将实体对象转换为DTO
     * @param seatDetail 座位详情实体
     * @return 座位详情DTO
     */
    SeatDetailDTO convertToDTO(SeatDetail seatDetail);
    
    /**
     * 将DTO转换为实体对象
     * @param seatDetailDTO 座位详情DTO
     * @return 座位详情实体
     */
    SeatDetail convertToEntity(SeatDetailDTO seatDetailDTO);
}