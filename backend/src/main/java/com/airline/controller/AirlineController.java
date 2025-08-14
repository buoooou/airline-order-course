package com.airline.controller;

import com.airline.dto.AirlineDto;
import com.airline.dto.ApiResponse;
import com.airline.entity.Airline;
import com.airline.repository.AirlineRepository;
import com.airline.mapper.AirlineMapper;
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
@RequestMapping("/api/airlines")
@Tag(name = "航空公司管理", description = "航空公司管理相关API")
public class AirlineController {

    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;

    @Autowired
    public AirlineController(AirlineRepository airlineRepository, AirlineMapper airlineMapper) {
        this.airlineRepository = airlineRepository;
        this.airlineMapper = airlineMapper;
    }

    @GetMapping
    @Operation(summary = "获取航空公司列表", description = "获取航空公司列表")
    public ResponseEntity<ApiResponse<List<AirlineDto>>> getAllAirlines() {
        List<AirlineDto> airlines = airlineMapper.toDtoList(
                airlineRepository.findByStatus(Airline.Status.ACTIVE)
        );
        return ResponseEntity.ok(ApiResponse.success(airlines));
    }

    @GetMapping("/paged")
    @Operation(summary = "分页获取航空公司", description = "分页获取航空公司列表")
    public ResponseEntity<ApiResponse<Page<AirlineDto>>> getAllAirlinesPaged(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AirlineDto> airlines = airlineRepository.findByStatus(Airline.Status.ACTIVE, pageable)
                .map(airlineMapper::toDto);
        return ResponseEntity.ok(ApiResponse.success(airlines));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取航空公司", description = "根据航空公司ID获取详情")
    public ResponseEntity<ApiResponse<AirlineDto>> getAirlineById(
            @Parameter(description = "航空公司ID") @PathVariable Long id) {
        Optional<AirlineDto> airline = airlineRepository.findById(id)
                .map(airlineMapper::toDto);
        if (airline.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(airline.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据代码获取航空公司", description = "根据航空公司代码获取详情")
    public ResponseEntity<ApiResponse<AirlineDto>> getAirlineByCode(
            @Parameter(description = "航空公司代码") @PathVariable String code) {
        Optional<AirlineDto> airline = airlineRepository.findByCode(code)
                .map(airlineMapper::toDto);
        if (airline.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(airline.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索航空公司", description = "根据关键词搜索航空公司")
    public ResponseEntity<ApiResponse<Page<AirlineDto>>> searchAirlines(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<AirlineDto> airlines = airlineRepository.findByKeyword(keyword, pageable)
                .map(airlineMapper::toDto);
        return ResponseEntity.ok(ApiResponse.success(airlines));
    }

    @PostMapping
    @Operation(summary = "创建航空公司", description = "创建新的航空公司")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AirlineDto>> createAirline(@RequestBody AirlineDto airlineDto) {
        Airline airline = airlineMapper.toEntity(airlineDto);
        Airline savedAirline = airlineRepository.save(airline);
        return ResponseEntity.ok(ApiResponse.success("航空公司创建成功", airlineMapper.toDto(savedAirline)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新航空公司信息", description = "更新航空公司信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AirlineDto>> updateAirline(
            @Parameter(description = "航空公司ID") @PathVariable Long id,
            @RequestBody AirlineDto airlineDto) {
        return airlineRepository.findById(id)
                .map(airline -> {
                    airlineMapper.updateEntityFromDto(airlineDto, airline);
                    Airline savedAirline = airlineRepository.save(airline);
                    return ResponseEntity.ok(ApiResponse.success("航空公司更新成功", airlineMapper.toDto(savedAirline)));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}