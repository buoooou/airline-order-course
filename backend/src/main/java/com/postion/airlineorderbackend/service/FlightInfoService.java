package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.FlightInfoDTO;
import com.postion.airlineorderbackend.entity.FlightInfo;
import com.postion.airlineorderbackend.enums.FlightStatus;
import com.postion.airlineorderbackend.repository.FlightInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 航班信息服务类
 * 提供航班信息相关的业务逻辑处理
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FlightInfoService {
    
    private final FlightInfoRepository flightInfoRepository;
    
    /**
     * 根据ID查找航班信息
     * @param id 航班ID
     * @return 航班信息DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<FlightInfoDTO> findById(Long id) {
        log.debug("根据ID查找航班信息: {}", id);
        
        return flightInfoRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    /**
     * 根据航班号查找航班信息
     * @param flightNumber 航班号
     * @return 航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findByFlightNumber(String flightNumber) {
        log.debug("根据航班号查找航班信息: {}", flightNumber);
        
        List<FlightInfo> flights = flightInfoRepository.findByFlightNumber(flightNumber);
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据航班号和出发时间查找航班（唯一）
     * @param flightNumber 航班号
     * @param departureTime 出发时间
     * @return 航班信息DTO（可能为空）
     */
    @Transactional(readOnly = true)
    public Optional<FlightInfoDTO> findByFlightNumberAndDepartureTime(String flightNumber, LocalDateTime departureTime) {
        log.debug("根据航班号和出发时间查找航班: {} - {}", flightNumber, departureTime);
        
        return flightInfoRepository.findByFlightNumberAndDepartureTime(flightNumber, departureTime)
                .map(this::convertToDTO);
    }
    
    /**
     * 根据出发地和目的地查找航班
     * @param departureAirport 出发机场代码
     * @param arrivalAirport 到达机场代码
     * @return 航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findByRoute(String departureAirport, String arrivalAirport) {
        log.debug("根据航线查找航班: {} -> {}", departureAirport, arrivalAirport);
        
        List<FlightInfo> flights = flightInfoRepository.findByDepartureAirportAndArrivalAirport(
                departureAirport, arrivalAirport);
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据航空公司查找航班
     * @param airline 航空公司名称
     * @return 航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findByAirline(String airline) {
        log.debug("根据航空公司查找航班: {}", airline);
        
        List<FlightInfo> flights = flightInfoRepository.findByAirline(airline);
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据航班状态查找航班
     * @param status 航班状态
     * @return 航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findByStatus(FlightStatus status) {
        log.debug("根据状态查找航班: {}", status);
        
        List<FlightInfo> flights = flightInfoRepository.findByStatus(status);
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 查找可预订的航班
     * @return 可预订的航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findBookableFlights() {
        log.debug("查找可预订的航班");
        
        List<FlightInfo> flights = flightInfoRepository.findBookableFlights();
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据出发时间范围查找航班
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findByDepartureTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("根据出发时间范围查找航班: {} - {}", startTime, endTime);
        
        List<FlightInfo> flights = flightInfoRepository.findByDepartureTimeBetween(startTime, endTime);
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 综合搜索航班
     * @param departureAirport 出发机场代码
     * @param arrivalAirport 到达机场代码
     * @param startTime 出发时间范围开始
     * @param endTime 出发时间范围结束
     * @param pageable 分页参数
     * @return 分页的航班信息DTO
     */
    @Transactional(readOnly = true)
    public Page<FlightInfoDTO> searchFlights(String departureAirport, String arrivalAirport,
                                            LocalDateTime startTime, LocalDateTime endTime,
                                            Pageable pageable) {
        log.debug("综合搜索航班: {} -> {}, {} - {}", departureAirport, arrivalAirport, startTime, endTime);
        
        Page<FlightInfo> flights = flightInfoRepository.searchFlights(
                departureAirport, arrivalAirport, startTime, endTime, pageable);
        
        return flights.map(this::convertToDTO);
    }
    
    /**
     * 根据价格范围查找航班
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("根据价格范围查找航班: {} - {}", minPrice, maxPrice);
        
        List<FlightInfo> flights = flightInfoRepository.findByPriceBetween(minPrice, maxPrice);
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 查找即将起飞的航班
     * @return 即将起飞的航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findUpcomingFlights() {
        log.debug("查找即将起飞的航班");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursLater = now.plusHours(2);
        
        List<FlightInfo> flights = flightInfoRepository.findUpcomingFlights(now, twoHoursLater);
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 查找座位紧张的航班
     * @return 座位紧张的航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> findLowAvailabilityFlights() {
        log.debug("查找座位紧张的航班");
        
        List<FlightInfo> flights = flightInfoRepository.findLowAvailabilityFlights();
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建新航班
     * @param flightInfoDTO 航班信息DTO
     * @return 创建的航班信息DTO
     * @throws IllegalArgumentException 如果航班已存在
     */
    public FlightInfoDTO createFlight(FlightInfoDTO flightInfoDTO) {
        log.info("创建新航班: {} - {}", flightInfoDTO.getFlightNumber(), flightInfoDTO.getDepartureTime());
        
        // 检查航班是否已存在
        Optional<FlightInfo> existingFlight = flightInfoRepository.findByFlightNumberAndDepartureTime(
                flightInfoDTO.getFlightNumber(), flightInfoDTO.getDepartureTime());
        
        if (existingFlight.isPresent()) {
            throw new IllegalArgumentException("航班已存在: " + flightInfoDTO.getFlightNumber() + 
                    " - " + flightInfoDTO.getDepartureTime());
        }
        
        // 创建航班实体
        FlightInfo flightInfo = FlightInfo.builder()
                .flightNumber(flightInfoDTO.getFlightNumber())
                .airline(flightInfoDTO.getAirline())
                .departureAirport(flightInfoDTO.getDepartureAirport())
                .arrivalAirport(flightInfoDTO.getArrivalAirport())
                .departureTime(flightInfoDTO.getDepartureTime())
                .arrivalTime(flightInfoDTO.getArrivalTime())
                .aircraftType(flightInfoDTO.getAircraftType())
                .price(flightInfoDTO.getPrice())
                .availableSeats(flightInfoDTO.getAvailableSeats())
                .totalSeats(flightInfoDTO.getTotalSeats())
                .status(flightInfoDTO.getStatus() != null ? flightInfoDTO.getStatus() : FlightStatus.ACTIVE)
                .build();
        
        FlightInfo savedFlight = flightInfoRepository.save(flightInfo);
        log.info("航班创建成功: {}", savedFlight.getFlightNumber());
        
        return convertToDTO(savedFlight);
    }
    
    /**
     * 更新航班信息
     * @param id 航班ID
     * @param flightInfoDTO 更新的航班信息DTO
     * @return 更新后的航班信息DTO
     * @throws IllegalArgumentException 如果航班不存在
     */
    public FlightInfoDTO updateFlight(Long id, FlightInfoDTO flightInfoDTO) {
        log.info("更新航班信息: {}", id);
        
        FlightInfo flightInfo = flightInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("航班不存在: " + id));
        
        // 更新航班信息
        if (flightInfoDTO.getFlightNumber() != null) {
            flightInfo.setFlightNumber(flightInfoDTO.getFlightNumber());
        }
        if (flightInfoDTO.getAirline() != null) {
            flightInfo.setAirline(flightInfoDTO.getAirline());
        }
        if (flightInfoDTO.getDepartureAirport() != null) {
            flightInfo.setDepartureAirport(flightInfoDTO.getDepartureAirport());
        }
        if (flightInfoDTO.getArrivalAirport() != null) {
            flightInfo.setArrivalAirport(flightInfoDTO.getArrivalAirport());
        }
        if (flightInfoDTO.getDepartureTime() != null) {
            flightInfo.setDepartureTime(flightInfoDTO.getDepartureTime());
        }
        if (flightInfoDTO.getArrivalTime() != null) {
            flightInfo.setArrivalTime(flightInfoDTO.getArrivalTime());
        }
        if (flightInfoDTO.getAircraftType() != null) {
            flightInfo.setAircraftType(flightInfoDTO.getAircraftType());
        }
        if (flightInfoDTO.getPrice() != null) {
            flightInfo.setPrice(flightInfoDTO.getPrice());
        }
        if (flightInfoDTO.getAvailableSeats() != null) {
            flightInfo.setAvailableSeats(flightInfoDTO.getAvailableSeats());
        }
        if (flightInfoDTO.getTotalSeats() != null) {
            flightInfo.setTotalSeats(flightInfoDTO.getTotalSeats());
        }
        if (flightInfoDTO.getStatus() != null) {
            flightInfo.setStatus(flightInfoDTO.getStatus());
        }
        
        FlightInfo updatedFlight = flightInfoRepository.save(flightInfo);
        log.info("航班信息更新成功: {}", updatedFlight.getFlightNumber());
        
        return convertToDTO(updatedFlight);
    }
    
    /**
     * 删除航班
     * @param id 航班ID
     * @return 是否删除成功
     */
    public boolean deleteFlight(Long id) {
        log.info("删除航班: {}", id);
        
        if (!flightInfoRepository.existsById(id)) {
            log.warn("航班不存在，无法删除: {}", id);
            return false;
        }
        
        flightInfoRepository.deleteById(id);
        log.info("航班删除成功: {}", id);
        return true;
    }
    
    /**
     * 预订座位
     * @param flightId 航班ID
     * @param seatCount 座位数量
     * @return 是否预订成功
     */
    public boolean bookSeats(Long flightId, int seatCount) {
        log.info("预订座位: 航班ID={}, 座位数={}", flightId, seatCount);
        
        Optional<FlightInfo> flightOpt = flightInfoRepository.findById(flightId);
        if (flightOpt.isEmpty()) {
            log.warn("航班不存在: {}", flightId);
            return false;
        }
        
        FlightInfo flight = flightOpt.get();
        if (flight.bookSeats(seatCount)) {
            flightInfoRepository.save(flight);
            log.info("座位预订成功: 航班={}, 座位数={}", flight.getFlightNumber(), seatCount);
            return true;
        } else {
            log.warn("座位预订失败: 航班={}, 可用座位={}, 请求座位={}", 
                    flight.getFlightNumber(), flight.getAvailableSeats(), seatCount);
            return false;
        }
    }
    
    /**
     * 释放座位
     * @param flightId 航班ID
     * @param seatCount 座位数量
     * @return 是否释放成功
     */
    public boolean releaseSeats(Long flightId, int seatCount) {
        log.info("释放座位: 航班ID={}, 座位数={}", flightId, seatCount);
        
        Optional<FlightInfo> flightOpt = flightInfoRepository.findById(flightId);
        if (flightOpt.isEmpty()) {
            log.warn("航班不存在: {}", flightId);
            return false;
        }
        
        FlightInfo flight = flightOpt.get();
        flight.releaseSeats(seatCount);
        flightInfoRepository.save(flight);
        
        log.info("座位释放成功: 航班={}, 座位数={}", flight.getFlightNumber(), seatCount);
        return true;
    }
    
    /**
     * 获取所有航班
     * @return 所有航班信息DTO列表
     */
    @Transactional(readOnly = true)
    public List<FlightInfoDTO> getAllFlights() {
        log.debug("获取所有航班");
        
        List<FlightInfo> flights = flightInfoRepository.findAll();
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 多条件搜索航班
     * @param flightNumber 航班号（可为空）
     * @param airline 航空公司（可为空）
     * @param departureAirport 出发机场（可为空）
     * @param arrivalAirport 到达机场（可为空）
     * @param status 航班状态（可为空）
     * @param pageable 分页参数
     * @return 分页的航班信息DTO
     */
    @Transactional(readOnly = true)
    public Page<FlightInfoDTO> findByMultipleConditions(String flightNumber, String airline,
                                                       String departureAirport, String arrivalAirport,
                                                       FlightStatus status, Pageable pageable) {
        log.debug("多条件搜索航班: 航班号={}, 航空公司={}, 出发地={}, 目的地={}, 状态={}", 
                flightNumber, airline, departureAirport, arrivalAirport, status);
        
        Page<FlightInfo> flights = flightInfoRepository.findByMultipleConditions(
                flightNumber, airline, departureAirport, arrivalAirport, status, pageable);
        
        return flights.map(this::convertToDTO);
    }
    
    /**
     * 将FlightInfo实体转换为FlightInfoDTO
     * @param flightInfo 航班信息实体
     * @return 航班信息DTO
     */
    private FlightInfoDTO convertToDTO(FlightInfo flightInfo) {
        FlightInfoDTO dto = FlightInfoDTO.builder()
                .id(flightInfo.getId())
                .flightNumber(flightInfo.getFlightNumber())
                .airline(flightInfo.getAirline())
                .departureAirport(flightInfo.getDepartureAirport())
                .arrivalAirport(flightInfo.getArrivalAirport())
                .departureTime(flightInfo.getDepartureTime())
                .arrivalTime(flightInfo.getArrivalTime())
                .aircraftType(flightInfo.getAircraftType())
                .price(flightInfo.getPrice())
                .availableSeats(flightInfo.getAvailableSeats())
                .totalSeats(flightInfo.getTotalSeats())
                .status(flightInfo.getStatus())
                .statusDescription(flightInfo.getStatus() != null ? flightInfo.getStatus().getDescription() : "")
                .createdAt(flightInfo.getCreatedAt())
                .updatedAt(flightInfo.getUpdatedAt())
                .build();
        
        // 计算衍生字段
        if (flightInfo.getDepartureTime() != null && flightInfo.getArrivalTime() != null) {
            dto.setFlightDurationMinutes(flightInfo.getFlightDurationMinutes());
        }
        
        if (flightInfo.getTotalSeats() != null && flightInfo.getTotalSeats() > 0 && 
            flightInfo.getAvailableSeats() != null) {
            dto.setOccupancyRate(flightInfo.getOccupancyRate());
        }
        
        dto.setBookable(flightInfo.isBookable());
        dto.setFullyBooked(flightInfo.isFullyBooked());
        
        return dto;
    }
}
