package com.airline.controller;

import com.airline.dto.ApiResponse;
import com.airline.dto.FlightDto;
import com.airline.dto.FlightSearchDto;
import com.airline.entity.Flight;
import com.airline.entity.Airline;
import com.airline.entity.Airport;
import com.airline.service.FlightService;
import com.airline.repository.AirlineRepository;
import com.airline.repository.AirportRepository;
import com.airline.repository.FlightRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
@Tag(name = "航班管理", description = "航班管理相关API")
public class FlightController {

    private final FlightService flightService;
    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;

    @Autowired
    public FlightController(FlightService flightService, FlightRepository flightRepository,
                          AirlineRepository airlineRepository, AirportRepository airportRepository) {
        this.flightService = flightService;
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
    }

    @PostMapping
    @Operation(summary = "创建航班", description = "创建新的航班")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FlightDto>> createFlight(@Valid @RequestBody FlightDto flightDto) {
        FlightDto flight = flightService.createFlight(flightDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("航班创建成功", flight));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取航班", description = "根据航班ID获取航班详情")
    public ResponseEntity<ApiResponse<FlightDto>> getFlightById(
            @Parameter(description = "航班ID") @PathVariable Long id) {
        Optional<FlightDto> flight = flightService.getFlightById(id);
        if (flight.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(flight.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/number/{flightNumber}")
    @Operation(summary = "根据航班号获取航班", description = "根据航班号获取航班详情")
    public ResponseEntity<ApiResponse<FlightDto>> getFlightByNumber(
            @Parameter(description = "航班号") @PathVariable String flightNumber) {
        Optional<FlightDto> flight = flightService.getFlightByNumber(flightNumber);
        if (flight.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(flight.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "获取航班列表", description = "分页获取航班列表")
    public ResponseEntity<ApiResponse<Page<FlightDto>>> getAllFlights(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "departureTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FlightDto> flights = flightService.getAllFlights(pageable);
        return ResponseEntity.ok(ApiResponse.success(flights));
    }

    @PostMapping("/search")
    @Operation(summary = "搜索航班", description = "根据搜索条件搜索航班")
    public ResponseEntity<ApiResponse<Page<FlightDto>>> searchFlights(
            @Valid @RequestBody FlightSearchDto searchDto,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").ascending());
        Page<FlightDto> flights = flightService.searchFlights(searchDto, pageable);
        return ResponseEntity.ok(ApiResponse.success(flights));
    }

    @GetMapping("/airline/{airlineId}")
    @Operation(summary = "根据航空公司获取航班", description = "根据航空公司ID获取航班列表")
    public ResponseEntity<ApiResponse<Page<FlightDto>>> getFlightsByAirline(
            @Parameter(description = "航空公司ID") @PathVariable Long airlineId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").ascending());
        Page<FlightDto> flights = flightService.getFlightsByAirline(airlineId, pageable);
        return ResponseEntity.ok(ApiResponse.success(flights));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取航班", description = "根据航班状态获取航班列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<FlightDto>>> getFlightsByStatus(
            @Parameter(description = "航班状态") @PathVariable Flight.Status status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").ascending());
        Page<FlightDto> flights = flightService.getFlightsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(flights));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新航班信息", description = "更新航班信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FlightDto>> updateFlight(
            @Parameter(description = "航班ID") @PathVariable Long id,
            @Valid @RequestBody FlightDto flightDto) {
        FlightDto updatedFlight = flightService.updateFlight(id, flightDto);
        return ResponseEntity.ok(ApiResponse.success("航班更新成功", updatedFlight));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新航班状态", description = "更新航班状态")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FlightDto>> updateFlightStatus(
            @Parameter(description = "航班ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam Flight.Status status) {
        FlightDto updatedFlight = flightService.updateFlightStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("状态更新成功", updatedFlight));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除航班", description = "删除航班")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(
            @Parameter(description = "航班ID") @PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok(ApiResponse.success("航班删除成功"));
    }

    @GetMapping("/available")
    @Operation(summary = "获取可用航班", description = "获取指定时间段内的可用航班")
    public ResponseEntity<ApiResponse<List<FlightDto>>> getAvailableFlights(
            @Parameter(description = "出发机场代码") @RequestParam String departureCode,
            @Parameter(description = "到达机场代码") @RequestParam String arrivalCode,
            @Parameter(description = "开始时间") @RequestParam 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "结束时间") @RequestParam 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        List<FlightDto> flights = flightService.getAvailableFlights(
                departureCode, arrivalCode, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(flights));
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "统计航班数量", description = "统计指定状态的航班数量")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> countFlightsByStatus(
            @Parameter(description = "航班状态") @PathVariable Flight.Status status) {
        long count = flightService.countFlightsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PostMapping("/{id}/seats/update")
    @Operation(summary = "更新座位可用性", description = "更新航班的可用座位数")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> updateSeatAvailability(
            @Parameter(description = "航班ID") @PathVariable Long id,
            @Parameter(description = "座位变化数量") @RequestParam int seatChange) {
        flightService.updateSeatAvailability(id, seatChange);
        return ResponseEntity.ok(ApiResponse.success("座位更新成功"));
    }


}