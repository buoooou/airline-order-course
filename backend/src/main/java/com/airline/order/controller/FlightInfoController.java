package com.airline.order.controller;

import com.airline.order.dto.ApiResponse;
import com.airline.order.dto.FlightInfoDTO;
import com.airline.order.service.FlightInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 航班信息控制器
 */
@RestController
@RequestMapping("/api/flights")
public class FlightInfoController {
    
    private final FlightInfoService flightInfoService;
    
    @Autowired
    public FlightInfoController(FlightInfoService flightInfoService) {
        this.flightInfoService = flightInfoService;
    }
    
    /**
     * 获取所有航班信息
     * @return 航班信息列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightInfoDTO>>> getAllFlights() {
        List<FlightInfoDTO> flights = flightInfoService.getAllFlights();
        return ResponseEntity.ok(new ApiResponse<>(true, "获取所有航班成功", flights));
    }
    
    /**
     * 根据ID获取航班信息
     * @param id 航班ID
     * @return 航班信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightInfoDTO>> getFlightById(@PathVariable Long id) {
        FlightInfoDTO flight = flightInfoService.getFlightById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "获取航班成功", flight));
    }
    
    /**
     * 根据航班号获取航班信息
     * @param flightNumber 航班号
     * @return 航班信息
     */
    @GetMapping("/number/{flightNumber}")
    public ResponseEntity<ApiResponse<FlightInfoDTO>> getFlightByNumber(@PathVariable String flightNumber) {
        FlightInfoDTO flight = flightInfoService.getFlightByNumber(flightNumber);
        return ResponseEntity.ok(new ApiResponse<>(true, "获取航班成功", flight));
    }
    
    /**
     * 创建新航班
     * @param flightInfoDTO 航班信息
     * @return 创建的航班信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FlightInfoDTO>> createFlight(@Valid @RequestBody FlightInfoDTO flightInfoDTO) {
        FlightInfoDTO createdFlight = flightInfoService.createFlight(flightInfoDTO);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "航班创建成功", createdFlight),
                HttpStatus.CREATED);
    }
    
    /**
     * 更新航班信息
     * @param id 航班ID
     * @param flightInfoDTO 航班信息
     * @return 更新后的航班信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FlightInfoDTO>> updateFlight(
            @PathVariable Long id, 
            @Valid @RequestBody FlightInfoDTO flightInfoDTO) {
        FlightInfoDTO updatedFlight = flightInfoService.updateFlight(id, flightInfoDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "航班更新成功", updatedFlight));
    }
    
    /**
     * 删除航班
     * @param id 航班ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(@PathVariable Long id) {
        flightInfoService.deleteFlight(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "航班删除成功", null));
    }
    
    /**
     * 根据出发和到达机场代码查询航班
     * @param departureCode 出发机场代码
     * @param arrivalCode 到达机场代码
     * @return 航班信息列表
     */
    @GetMapping("/search/route")
    public ResponseEntity<ApiResponse<List<FlightInfoDTO>>> searchFlightsByRoute(
            @RequestParam String departureCode, 
            @RequestParam String arrivalCode) {
        List<FlightInfoDTO> flights = flightInfoService.searchFlightsByRoute(departureCode, arrivalCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "航班查询成功", flights));
    }
    
    /**
     * 根据出发日期查询航班
     * @param date 出发日期
     * @return 航班信息列表
     */
    @GetMapping("/search/date")
    public ResponseEntity<ApiResponse<List<FlightInfoDTO>>> searchFlightsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<FlightInfoDTO> flights = flightInfoService.searchFlightsByDate(date);
        return ResponseEntity.ok(new ApiResponse<>(true, "航班查询成功", flights));
    }
    
    /**
     * 根据航班号关键字模糊查询
     * @param keyword 航班号关键字
     * @return 航班信息列表
     */
    @GetMapping("/search/number")
    public ResponseEntity<ApiResponse<List<FlightInfoDTO>>> searchFlightsByNumberKeyword(
            @RequestParam String keyword) {
        List<FlightInfoDTO> flights = flightInfoService.searchFlightsByNumberKeyword(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "航班查询成功", flights));
    }
    
    /**
     * 根据飞行时长范围查询航班
     * @param minDuration 最小飞行时长（分钟）
     * @param maxDuration 最大飞行时长（分钟）
     * @return 航班信息列表
     */
    @GetMapping("/search/duration")
    public ResponseEntity<ApiResponse<List<FlightInfoDTO>>> searchFlightsByDuration(
            @RequestParam Integer minDuration, 
            @RequestParam Integer maxDuration) {
        List<FlightInfoDTO> flights = flightInfoService.searchFlightsByDuration(minDuration, maxDuration);
        return ResponseEntity.ok(new ApiResponse<>(true, "航班查询成功", flights));
    }
}