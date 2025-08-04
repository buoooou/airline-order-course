package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
@Tag(name = "航班管理", description = "航班查询和预订相关接口")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping("/search")
    @Operation(summary = "搜索航班", description = "根据出发地、目的地和日期搜索航班")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchFlights(
            @RequestParam String departureCity,
            @RequestParam String arrivalCity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate) {
        try {
            List<Map<String, Object>> flights = flightService.searchFlights(departureCity, arrivalCity, departureDate);
            return ResponseEntity.ok(ApiResponse.success("搜索航班成功", flights));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{flightNumber}")
    @Operation(summary = "获取航班详情", description = "根据航班号获取航班详细信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFlightDetails(@PathVariable String flightNumber) {
        try {
            Map<String, Object> flight = flightService.getFlightDetails(flightNumber);
            return ResponseEntity.ok(ApiResponse.success("获取航班详情成功", flight));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{flightNumber}/book")
    @Operation(summary = "预订航班", description = "预订指定航班")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bookFlight(
            @PathVariable String flightNumber,
            @RequestParam int seats,
            @RequestParam String passengerName) {
        try {
            Map<String, Object> booking = flightService.bookFlight(flightNumber, seats, passengerName);
            return ResponseEntity.ok(ApiResponse.success("预订航班成功", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{flightNumber}/availability")
    @Operation(summary = "检查座位可用性", description = "检查指定航班的座位可用性")
    public ResponseEntity<ApiResponse<Boolean>> checkSeatAvailability(
            @PathVariable String flightNumber,
            @RequestParam int seats) {
        try {
            boolean available = flightService.checkSeatAvailability(flightNumber, seats);
            return ResponseEntity.ok(ApiResponse.success("检查座位可用性成功", available));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 