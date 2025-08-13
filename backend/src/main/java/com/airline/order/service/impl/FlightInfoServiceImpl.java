package com.airline.order.service.impl;

import com.airline.order.dto.FlightInfoDTO;
import com.airline.order.dto.OrderDTO;
import com.airline.order.entity.FlightInfo;
import com.airline.order.entity.Order;
import com.airline.order.exception.ResourceNotFoundException;
import com.airline.order.repository.FlightInfoRepository;
import com.airline.order.service.FlightInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 航班信息服务实现类
 */
@Service
public class FlightInfoServiceImpl implements FlightInfoService {
    
    private final FlightInfoRepository flightInfoRepository;
    
    @Autowired
    public FlightInfoServiceImpl(FlightInfoRepository flightInfoRepository) {
        this.flightInfoRepository = flightInfoRepository;
    }
    
    @Override
    public List<FlightInfoDTO> getAllFlights() {
        return flightInfoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public FlightInfoDTO getFlightById(Long id) {
        FlightInfo flightInfo = flightInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("航班不存在，ID: " + id));
        return convertToDTO(flightInfo);
    }
    
    @Override
    public FlightInfoDTO getFlightByNumber(String flightNumber) {
        FlightInfo flightInfo = flightInfoRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new ResourceNotFoundException("航班不存在，航班号: " + flightNumber));
        return convertToDTO(flightInfo);
    }
    
    @Override
    @Transactional
    public FlightInfoDTO createFlight(FlightInfoDTO flightInfoDTO) {
        FlightInfo flightInfo = convertToEntity(flightInfoDTO);
        FlightInfo savedFlightInfo = flightInfoRepository.save(flightInfo);
        return convertToDTO(savedFlightInfo);
    }
    
    @Override
    @Transactional
    public FlightInfoDTO updateFlight(Long id, FlightInfoDTO flightInfoDTO) {
        FlightInfo existingFlight = flightInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("航班不存在，ID: " + id));
        
        // 更新航班信息
        existingFlight.setFlightNumber(flightInfoDTO.getFlightNumber());
        existingFlight.setDepartureAirportCode(flightInfoDTO.getDepartureAirportCode());
        existingFlight.setDepartureAirportName(flightInfoDTO.getDepartureAirportName());
        existingFlight.setArrivalAirportCode(flightInfoDTO.getArrivalAirportCode());
        existingFlight.setArrivalAirportName(flightInfoDTO.getArrivalAirportName());
        existingFlight.setDepartureTime(flightInfoDTO.getDepartureTime());
        existingFlight.setArrivalTime(flightInfoDTO.getArrivalTime());
        existingFlight.setFlightDuration(flightInfoDTO.getFlightDuration());
        
        FlightInfo updatedFlight = flightInfoRepository.save(existingFlight);
        return convertToDTO(updatedFlight);
    }
    
    @Override
    @Transactional
    public void deleteFlight(Long id) {
        if (!flightInfoRepository.existsById(id)) {
            throw new ResourceNotFoundException("航班不存在，ID: " + id);
        }
        flightInfoRepository.deleteById(id);
    }
    
    @Override
    public List<FlightInfoDTO> searchFlightsByRoute(String departureCode, String arrivalCode) {
        return flightInfoRepository.findByDepartureAirportCodeAndArrivalAirportCode(departureCode, arrivalCode)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FlightInfoDTO> searchFlightsByDate(LocalDateTime departureDate) {
        return flightInfoRepository.findByDepartureDate(departureDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FlightInfoDTO> searchFlightsByNumberKeyword(String keyword) {
        return flightInfoRepository.findByFlightNumberContaining(keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FlightInfoDTO> searchFlightsByDuration(Integer minDuration, Integer maxDuration) {
        return flightInfoRepository.findByFlightDurationBetween(minDuration, maxDuration)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public FlightInfoDTO convertToDTO(FlightInfo flightInfo) {
        if (flightInfo == null) {
            return null;
        }
        
        FlightInfoDTO dto = new FlightInfoDTO();
        dto.setId(flightInfo.getId());
        dto.setFlightNumber(flightInfo.getFlightNumber());
        dto.setDepartureAirportCode(flightInfo.getDepartureAirportCode());
        dto.setDepartureAirportName(flightInfo.getDepartureAirportName());
        dto.setArrivalAirportCode(flightInfo.getArrivalAirportCode());
        dto.setArrivalAirportName(flightInfo.getArrivalAirportName());
        dto.setDepartureTime(flightInfo.getDepartureTime());
        dto.setArrivalTime(flightInfo.getArrivalTime());
        dto.setFlightDuration(flightInfo.getFlightDuration());
        
        // 不加载订单信息，避免循环依赖
        // 如果需要订单信息，可以在特定场景下单独处理
        
        return dto;
    }
    
    @Override
    public FlightInfo convertToEntity(FlightInfoDTO flightInfoDTO) {
        if (flightInfoDTO == null) {
            return null;
        }
        
        FlightInfo entity = new FlightInfo();
        
        // 如果是更新操作，设置ID
        if (flightInfoDTO.getId() != null) {
            entity.setId(flightInfoDTO.getId());
        }
        
        entity.setFlightNumber(flightInfoDTO.getFlightNumber());
        entity.setDepartureAirportCode(flightInfoDTO.getDepartureAirportCode());
        entity.setDepartureAirportName(flightInfoDTO.getDepartureAirportName());
        entity.setArrivalAirportCode(flightInfoDTO.getArrivalAirportCode());
        entity.setArrivalAirportName(flightInfoDTO.getArrivalAirportName());
        entity.setDepartureTime(flightInfoDTO.getDepartureTime());
        entity.setArrivalTime(flightInfoDTO.getArrivalTime());
        entity.setFlightDuration(flightInfoDTO.getFlightDuration());
        
        // 不处理订单信息，避免循环依赖
        
        return entity;
    }
}