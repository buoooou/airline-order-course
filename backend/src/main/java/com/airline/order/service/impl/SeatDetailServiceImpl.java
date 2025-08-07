package com.airline.order.service.impl;

import com.airline.order.dto.SeatDetailDTO;
import com.airline.order.entity.FlightInfo;
import com.airline.order.entity.SeatDetail;
import com.airline.order.entity.SeatDetail.SeatStatus;
import com.airline.order.exception.ResourceNotFoundException;
import com.airline.order.repository.FlightInfoRepository;
import com.airline.order.repository.SeatDetailRepository;
import com.airline.order.service.SeatDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 座位详情服务实现类
 */
@Service
public class SeatDetailServiceImpl implements SeatDetailService {

    @Autowired
    private SeatDetailRepository seatDetailRepository;
    
    @Autowired
    private FlightInfoRepository flightInfoRepository;
    
    @Override
    public List<SeatDetailDTO> getAllSeatDetails() {
        return seatDetailRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public SeatDetailDTO getSeatDetailById(Long id) {
        SeatDetail seatDetail = seatDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("座位详情不存在，ID: " + id));
        return convertToDTO(seatDetail);
    }
    
    @Override
    public List<SeatDetailDTO> getSeatDetailsByFlightId(Long flightId) {
        return seatDetailRepository.findByFlightInfoId(flightId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SeatDetailDTO> getSeatDetailsByFlightIdAndStatus(Long flightId, SeatStatus status) {
        return seatDetailRepository.findByFlightInfoIdAndSeatStatus(flightId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SeatDetailDTO createSeatDetail(SeatDetailDTO seatDetailDTO) {
        SeatDetail seatDetail = convertToEntity(seatDetailDTO);
        SeatDetail savedSeatDetail = seatDetailRepository.save(seatDetail);
        return convertToDTO(savedSeatDetail);
    }
    
    @Override
    @Transactional
    public List<SeatDetailDTO> createSeatDetails(List<SeatDetailDTO> seatDetailDTOs) {
        List<SeatDetail> seatDetails = seatDetailDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        
        List<SeatDetail> savedSeatDetails = seatDetailRepository.saveAll(seatDetails);
        
        return savedSeatDetails.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SeatDetailDTO updateSeatDetail(Long id, SeatDetailDTO seatDetailDTO) {
        SeatDetail existingSeatDetail = seatDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("座位详情不存在，ID: " + id));
        
        // 更新字段
        existingSeatDetail.setSeatNumber(seatDetailDTO.getSeatNumber());
        existingSeatDetail.setSeatType(seatDetailDTO.getSeatType());
        existingSeatDetail.setSeatStatus(seatDetailDTO.getSeatStatus());
        existingSeatDetail.setPrice(seatDetailDTO.getPrice());
        
        // 如果航班ID发生变化，需要更新关联
        if (!existingSeatDetail.getFlightInfo().getId().equals(seatDetailDTO.getFlightId())) {
            FlightInfo flightInfo = flightInfoRepository.findById(seatDetailDTO.getFlightId())
                    .orElseThrow(() -> new ResourceNotFoundException("航班不存在，ID: " + seatDetailDTO.getFlightId()));
            existingSeatDetail.setFlightInfo(flightInfo);
        }
        
        SeatDetail updatedSeatDetail = seatDetailRepository.save(existingSeatDetail);
        return convertToDTO(updatedSeatDetail);
    }
    
    @Override
    @Transactional
    public SeatDetailDTO updateSeatStatus(Long id, SeatStatus status) {
        SeatDetail existingSeatDetail = seatDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("座位详情不存在，ID: " + id));
        
        existingSeatDetail.setSeatStatus(status);
        SeatDetail updatedSeatDetail = seatDetailRepository.save(existingSeatDetail);
        return convertToDTO(updatedSeatDetail);
    }
    
    @Override
    @Transactional
    public void deleteSeatDetail(Long id) {
        if (!seatDetailRepository.existsById(id)) {
            throw new ResourceNotFoundException("座位详情不存在，ID: " + id);
        }
        seatDetailRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void deleteSeatDetailsByFlightId(Long flightId) {
        seatDetailRepository.deleteByFlightInfoId(flightId);
    }
    
    @Override
    public SeatDetailDTO convertToDTO(SeatDetail seatDetail) {
        if (seatDetail == null) {
            return null;
        }
        
        SeatDetailDTO dto = new SeatDetailDTO();
        dto.setId(seatDetail.getId());
        dto.setFlightId(seatDetail.getFlightInfo().getId());
        dto.setFlightNumber(seatDetail.getFlightInfo().getFlightNumber());
        dto.setSeatNumber(seatDetail.getSeatNumber());
        dto.setSeatType(seatDetail.getSeatType());
        dto.setSeatStatus(seatDetail.getSeatStatus());
        dto.setPrice(seatDetail.getPrice());
        dto.setCreatedAt(seatDetail.getCreatedAt());
        dto.setUpdatedAt(seatDetail.getUpdatedAt());
        
        return dto;
    }
    
    @Override
    public SeatDetail convertToEntity(SeatDetailDTO seatDetailDTO) {
        if (seatDetailDTO == null) {
            return null;
        }
        
        SeatDetail entity = new SeatDetail();
        
        if (seatDetailDTO.getId() != null) {
            entity.setId(seatDetailDTO.getId());
        }
        
        // 设置航班信息
        FlightInfo flightInfo = flightInfoRepository.findById(seatDetailDTO.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("航班不存在，ID: " + seatDetailDTO.getFlightId()));
        entity.setFlightInfo(flightInfo);
        
        entity.setSeatNumber(seatDetailDTO.getSeatNumber());
        entity.setSeatType(seatDetailDTO.getSeatType());
        entity.setSeatStatus(seatDetailDTO.getSeatStatus());
        entity.setPrice(seatDetailDTO.getPrice());
        
        // 如果是新实体，设置创建和更新时间
        if (entity.getId() == null) {
            LocalDateTime now = LocalDateTime.now();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
        } else {
            // 如果是更新，只设置更新时间
            entity.setUpdatedAt(LocalDateTime.now());
        }
        
        return entity;
    }
}