package com.airline.controller;

import com.airline.dto.ApiResponse;
import com.airline.dto.PassengerDto;
import com.airline.security.UserPrincipal;
import com.airline.service.PassengerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/passengers")
@Tag(name = "旅客管理", description = "旅客管理相关API")
public class PassengerController {

    private final PassengerService passengerService;

    @Autowired
    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    @Operation(summary = "创建旅客", description = "创建新的旅客信息")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PassengerDto>> createPassenger(
            @Valid @RequestBody PassengerDto passengerDto,
            Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            userId = userPrincipal.getId();
        }
        
        PassengerDto passenger = passengerService.createPassenger(passengerDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("旅客创建成功", passenger));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取旅客", description = "根据旅客ID获取旅客详情")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<PassengerDto>> getPassengerById(
            @Parameter(description = "旅客ID") @PathVariable Long id) {
        Optional<PassengerDto> passenger = passengerService.getPassengerById(id);
        if (passenger.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(passenger.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "获取旅客列表", description = "分页获取旅客列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<PassengerDto>>> getAllPassengers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PassengerDto> passengers = passengerService.getAllPassengers(pageable);
        return ResponseEntity.ok(ApiResponse.success(passengers));
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的旅客信息", description = "获取当前用户的旅客信息")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PassengerDto>>> getMyPassengers(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<PassengerDto> passengers = passengerService.getPassengersByUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(passengers));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索旅客", description = "根据关键词搜索旅客")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<PassengerDto>>> searchPassengers(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PassengerDto> passengers = passengerService.searchPassengers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(passengers));
    }

    @GetMapping("/passport/{passportNumber}")
    @Operation(summary = "根据护照号获取旅客", description = "根据护照号获取旅客信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PassengerDto>> getPassengerByPassport(
            @Parameter(description = "护照号") @PathVariable String passportNumber) {
        Optional<PassengerDto> passenger = passengerService.getPassengerByPassport(passportNumber);
        if (passenger.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(passenger.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/idcard/{idCardNumber}")
    @Operation(summary = "根据身份证号获取旅客", description = "根据身份证号获取旅客信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PassengerDto>> getPassengerByIdCard(
            @Parameter(description = "身份证号") @PathVariable String idCardNumber) {
        Optional<PassengerDto> passenger = passengerService.getPassengerByIdCard(idCardNumber);
        if (passenger.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(passenger.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新旅客信息", description = "更新旅客信息")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PassengerDto>> updatePassenger(
            @Parameter(description = "旅客ID") @PathVariable Long id,
            @Valid @RequestBody PassengerDto passengerDto) {
        PassengerDto updatedPassenger = passengerService.updatePassenger(id, passengerDto);
        return ResponseEntity.ok(ApiResponse.success("旅客信息更新成功", updatedPassenger));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除旅客", description = "删除旅客信息")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePassenger(
            @Parameter(description = "旅客ID") @PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.ok(ApiResponse.success("旅客删除成功"));
    }

    @GetMapping("/check/passport/{passportNumber}")
    @Operation(summary = "检查护照号是否存在", description = "检查护照号是否已被使用")
    public ResponseEntity<ApiResponse<Boolean>> checkPassportNumber(
            @Parameter(description = "护照号") @PathVariable String passportNumber) {
        boolean exists = passengerService.existsByPassportNumber(passportNumber);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/check/idcard/{idCardNumber}")
    @Operation(summary = "检查身份证号是否存在", description = "检查身份证号是否已被使用")
    public ResponseEntity<ApiResponse<Boolean>> checkIdCardNumber(
            @Parameter(description = "身份证号") @PathVariable String idCardNumber) {
        boolean exists = passengerService.existsByIdCardNumber(idCardNumber);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}