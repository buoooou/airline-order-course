package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.FlightInfoDTO;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.enums.FlightStatus;
import com.postion.airlineorderbackend.service.AuthService;
import com.postion.airlineorderbackend.service.FlightInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 航班信息控制器
 * 提供航班信息管理相关的REST API
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "航班管理", description = "航班信息相关API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FlightInfoController {
    
    private final FlightInfoService flightInfoService;
    private final AuthService authService;
    
    /**
     * 获取所有航班信息
     * @return 航班信息列表
     */
    @GetMapping
    @Operation(summary = "获取所有航班", description = "获取所有航班信息列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    public ResponseEntity<?> getAllFlights() {
        log.debug("获取所有航班请求");
        
        try {
            List<FlightInfoDTO> flights = flightInfoService.getAllFlights();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取航班列表成功");
            result.put("data", flights);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("获取所有航班异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取航班列表失败");
            error.put("error", "GET_FLIGHTS_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 根据ID获取航班详情
     * @param id 航班ID
     * @return 航班详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取航班详情", description = "根据航班ID获取航班详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "航班不存在"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    public ResponseEntity<?> getFlightById(
            @Parameter(description = "航班ID", required = true) @PathVariable Long id) {
        
        log.debug("获取航班详情请求: {}", id);
        
        try {
            Optional<FlightInfoDTO> flightOpt = flightInfoService.findById(id);
            
            if (flightOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "航班不存在");
                error.put("error", "FLIGHT_NOT_FOUND");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取航班详情成功");
            result.put("data", flightOpt.get());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("获取航班详情异常: {}", id, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取航班详情失败");
            error.put("error", "GET_FLIGHT_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 根据航班号搜索航班
     * @param flightNumber 航班号
     * @return 航班信息列表
     */
    @GetMapping("/search/number/{flightNumber}")
    @Operation(summary = "根据航班号搜索", description = "根据航班号搜索航班信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
            @ApiResponse(responseCode = "500", description = "搜索失败")
    })
    public ResponseEntity<?> searchByFlightNumber(
            @Parameter(description = "航班号", required = true) @PathVariable String flightNumber) {
        
        log.debug("根据航班号搜索航班: {}", flightNumber);
        
        try {
            List<FlightInfoDTO> flights = flightInfoService.findByFlightNumber(flightNumber);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "搜索航班成功");
            result.put("data", flights);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("根据航班号搜索异常: {}", flightNumber, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "搜索航班失败");
            error.put("error", "SEARCH_FLIGHT_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 根据航线搜索航班
     * @param departureAirport 出发机场代码
     * @param arrivalAirport 到达机场代码
     * @return 航班信息列表
     */
    @GetMapping("/search/route")
    @Operation(summary = "根据航线搜索", description = "根据出发地和目的地搜索航班")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
            @ApiResponse(responseCode = "400", description = "参数无效"),
            @ApiResponse(responseCode = "500", description = "搜索失败")
    })
    public ResponseEntity<?> searchByRoute(
            @Parameter(description = "出发机场代码", required = true) @RequestParam String departureAirport,
            @Parameter(description = "到达机场代码", required = true) @RequestParam String arrivalAirport) {
        
        log.debug("根据航线搜索航班: {} -> {}", departureAirport, arrivalAirport);
        
        try {
            if (departureAirport == null || departureAirport.trim().isEmpty() ||
                arrivalAirport == null || arrivalAirport.trim().isEmpty()) {
                
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "出发地和目的地不能为空");
                error.put("error", "INVALID_PARAMETERS");
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            List<FlightInfoDTO> flights = flightInfoService.findByRoute(
                    departureAirport.trim(), arrivalAirport.trim());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "搜索航班成功");
            result.put("data", flights);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("根据航线搜索异常: {} -> {}", departureAirport, arrivalAirport, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "搜索航班失败");
            error.put("error", "SEARCH_ROUTE_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 综合搜索航班
     * @param departureAirport 出发机场代码
     * @param arrivalAirport 到达机场代码
     * @param startTime 出发时间范围开始
     * @param endTime 出发时间范围结束
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序字段
     * @return 分页的航班信息
     */
    @GetMapping("/search")
    @Operation(summary = "综合搜索航班", description = "根据多个条件搜索航班信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
            @ApiResponse(responseCode = "400", description = "参数无效"),
            @ApiResponse(responseCode = "500", description = "搜索失败")
    })
    public ResponseEntity<?> searchFlights(
            @Parameter(description = "出发机场代码") @RequestParam(required = false) String departureAirport,
            @Parameter(description = "到达机场代码") @RequestParam(required = false) String arrivalAirport,
            @Parameter(description = "出发时间范围开始") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "出发时间范围结束") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "departureTime") String sort) {
        
        log.debug("综合搜索航班: {} -> {}, {} - {}", departureAirport, arrivalAirport, startTime, endTime);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
            Page<FlightInfoDTO> flights = flightInfoService.searchFlights(
                    departureAirport, arrivalAirport, startTime, endTime, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "搜索航班成功");
            result.put("data", flights);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("综合搜索航班异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "搜索航班失败");
            error.put("error", "SEARCH_FLIGHTS_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 获取可预订的航班
     * @return 可预订的航班列表
     */
    @GetMapping("/bookable")
    @Operation(summary = "获取可预订航班", description = "获取所有可预订的航班信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    public ResponseEntity<?> getBookableFlights() {
        log.debug("获取可预订航班请求");
        
        try {
            List<FlightInfoDTO> flights = flightInfoService.findBookableFlights();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取可预订航班成功");
            result.put("data", flights);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("获取可预订航班异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "获取可预订航班失败");
            error.put("error", "GET_BOOKABLE_FLIGHTS_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 根据价格范围搜索航班
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 航班信息列表
     */
    @GetMapping("/search/price")
    @Operation(summary = "根据价格搜索", description = "根据价格范围搜索航班")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
            @ApiResponse(responseCode = "400", description = "价格参数无效"),
            @ApiResponse(responseCode = "500", description = "搜索失败")
    })
    public ResponseEntity<?> searchByPriceRange(
            @Parameter(description = "最低价格") @RequestParam BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam BigDecimal maxPrice) {
        
        log.debug("根据价格范围搜索航班: {} - {}", minPrice, maxPrice);
        
        try {
            if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(minPrice) < 0) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "价格参数无效");
                error.put("error", "INVALID_PRICE_RANGE");
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            List<FlightInfoDTO> flights = flightInfoService.findByPriceRange(minPrice, maxPrice);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "根据价格搜索航班成功");
            result.put("data", flights);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("根据价格搜索异常: {} - {}", minPrice, maxPrice, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "根据价格搜索失败");
            error.put("error", "SEARCH_BY_PRICE_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 创建新航班（管理员功能）
     * @param flightInfoDTO 航班信息
     * @param authorizationHeader Authorization头部
     * @return 创建的航班信息
     */
    @PostMapping
    @Operation(summary = "创建航班", description = "创建新的航班信息（管理员功能）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数无效"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限操作"),
            @ApiResponse(responseCode = "500", description = "创建失败")
    })
    public ResponseEntity<?> createFlight(
            @Valid @RequestBody FlightInfoDTO flightInfoDTO,
            @Parameter(description = "Authorization头部", required = true) 
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("创建航班请求: {}", flightInfoDTO.getFlightNumber());
        
        try {
            // 验证用户认证
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            // 检查权限：只有管理员可以创建航班
            if (!currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "无权限创建航班");
                error.put("error", "PERMISSION_DENIED");
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            FlightInfoDTO createdFlight = flightInfoService.createFlight(flightInfoDTO);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "航班创建成功");
            result.put("data", createdFlight);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("航班创建失败: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", "INVALID_FLIGHT_DATA");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            log.error("航班创建异常", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "航班创建失败");
            error.put("error", "CREATE_FLIGHT_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 更新航班信息（管理员功能）
     * @param id 航班ID
     * @param flightInfoDTO 更新的航班信息
     * @param authorizationHeader Authorization头部
     * @return 更新后的航班信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新航班", description = "更新航班信息（管理员功能）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数无效"),
            @ApiResponse(responseCode = "404", description = "航班不存在"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限操作"),
            @ApiResponse(responseCode = "500", description = "更新失败")
    })
    public ResponseEntity<?> updateFlight(
            @Parameter(description = "航班ID", required = true) @PathVariable Long id,
            @Valid @RequestBody FlightInfoDTO flightInfoDTO,
            @Parameter(description = "Authorization头部", required = true) 
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("更新航班请求: {}", id);
        
        try {
            // 验证用户认证
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            // 检查权限：只有管理员可以更新航班
            if (!currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "无权限更新航班");
                error.put("error", "PERMISSION_DENIED");
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            FlightInfoDTO updatedFlight = flightInfoService.updateFlight(id, flightInfoDTO);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "航班更新成功");
            result.put("data", updatedFlight);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("航班更新失败: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", "INVALID_FLIGHT_DATA");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            log.error("航班更新异常: {}", id, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "航班更新失败");
            error.put("error", "UPDATE_FLIGHT_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 删除航班（管理员功能）
     * @param id 航班ID
     * @param authorizationHeader Authorization头部
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除航班", description = "删除航班信息（管理员功能）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "航班不存在"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限操作"),
            @ApiResponse(responseCode = "500", description = "删除失败")
    })
    public ResponseEntity<?> deleteFlight(
            @Parameter(description = "航班ID", required = true) @PathVariable Long id,
            @Parameter(description = "Authorization头部", required = true) 
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("删除航班请求: {}", id);
        
        try {
            // 验证用户认证
            Optional<UserDTO> currentUserOpt = authService.validateAuthorizationHeader(authorizationHeader);
            if (currentUserOpt.isEmpty()) {
                return createUnauthorizedResponse();
            }
            
            UserDTO currentUser = currentUserOpt.get();
            
            // 检查权限：只有管理员可以删除航班
            if (!currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "无权限删除航班");
                error.put("error", "PERMISSION_DENIED");
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            boolean deleted = flightInfoService.deleteFlight(id);
            
            if (deleted) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "航班删除成功");
                
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "航班不存在");
                error.put("error", "FLIGHT_NOT_FOUND");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
        } catch (Exception e) {
            log.error("航班删除异常: {}", id, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "航班删除失败");
            error.put("error", "DELETE_FLIGHT_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 创建未认证响应
     * @return 未认证响应
     */
    private ResponseEntity<?> createUnauthorizedResponse() {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "未认证或令牌无效");
        error.put("error", "UNAUTHORIZED");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
