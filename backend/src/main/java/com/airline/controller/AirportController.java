package com.airline.controller;

import com.airline.dto.AirportDto;
import com.airline.dto.ApiResponse;
import com.airline.entity.Airport;
import com.airline.repository.AirportRepository;
import com.airline.mapper.AirportMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airports")
@Tag(name = "机场管理", description = "机场管理相关API")
public class AirportController {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    @Autowired
    public AirportController(AirportRepository airportRepository, AirportMapper airportMapper) {
        this.airportRepository = airportRepository;
        this.airportMapper = airportMapper;
    }

    @GetMapping
    @Operation(summary = "获取机场列表", description = "分页获取机场列表")
    public ResponseEntity<ApiResponse<Page<AirportDto>>> getAllAirports(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AirportDto> airports = airportRepository.findByStatus(Airport.Status.ACTIVE, pageable)
                .map(airportMapper::toDto);
        return ResponseEntity.ok(ApiResponse.success(airports));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取机场", description = "根据机场ID获取机场详情")
    public ResponseEntity<ApiResponse<AirportDto>> getAirportById(
            @Parameter(description = "机场ID") @PathVariable Long id) {
        Optional<AirportDto> airport = airportRepository.findById(id)
                .map(airportMapper::toDto);
        if (airport.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(airport.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据代码获取机场", description = "根据机场代码获取机场详情")
    public ResponseEntity<ApiResponse<AirportDto>> getAirportByCode(
            @Parameter(description = "机场代码") @PathVariable String code) {
        Optional<AirportDto> airport = airportRepository.findByCode(code)
                .map(airportMapper::toDto);
        if (airport.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(airport.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/countries")
    @Operation(summary = "获取国家列表", description = "获取所有可用的国家列表")
    public ResponseEntity<ApiResponse<List<String>>> getCountries() {
        List<String> countries = airportRepository.findDistinctCountriesByStatus(Airport.Status.ACTIVE);
        return ResponseEntity.ok(ApiResponse.success(countries));
    }

    @GetMapping("/cities")
    @Operation(summary = "获取城市列表", description = "根据国家获取城市列表")
    public ResponseEntity<ApiResponse<List<String>>> getCitiesByCountry(
            @Parameter(description = "国家") @RequestParam String country) {
        List<String> cities = airportRepository.findDistinctCitiesByCountryAndStatus(country, Airport.Status.ACTIVE);
        return ResponseEntity.ok(ApiResponse.success(cities));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索机场", description = "根据关键词搜索机场")
    public ResponseEntity<ApiResponse<Page<AirportDto>>> searchAirports(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<AirportDto> airports = airportRepository.findByKeyword(keyword, pageable)
                .map(airportMapper::toDto);
        return ResponseEntity.ok(ApiResponse.success(airports));
    }

    @PostMapping
    @Operation(summary = "创建机场", description = "创建新的机场")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AirportDto>> createAirport(@RequestBody AirportDto airportDto) {
        Airport airport = airportMapper.toEntity(airportDto);
        Airport savedAirport = airportRepository.save(airport);
        return ResponseEntity.ok(ApiResponse.success("机场创建成功", airportMapper.toDto(savedAirport)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新机场信息", description = "更新机场信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AirportDto>> updateAirport(
            @Parameter(description = "机场ID") @PathVariable Long id,
            @RequestBody AirportDto airportDto) {
        return airportRepository.findById(id)
                .map(airport -> {
                    airportMapper.updateEntityFromDto(airportDto, airport);
                    Airport savedAirport = airportRepository.save(airport);
                    return ResponseEntity.ok(ApiResponse.success("机场更新成功", airportMapper.toDto(savedAirport)));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}