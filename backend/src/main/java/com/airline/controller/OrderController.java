package com.airline.controller;

import com.airline.dto.ApiResponse;
import com.airline.dto.OrderCreateDto;
import com.airline.dto.OrderDto;
import com.airline.entity.Order;
import com.airline.security.UserPrincipal;
import com.airline.service.OrderService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单管理相关API")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的订单")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @Valid @RequestBody OrderCreateDto createDto,
            Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            userId = userPrincipal.getId();
        }
        
        OrderDto order = orderService.createOrder(createDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("订单创建成功", order));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取订单", description = "根据订单ID获取订单详情")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @orderService.getOrderById(#id).map(o -> o.userId).orElse(null) == authentication.principal.id)")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        Optional<OrderDto> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(order.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "根据订单号获取订单", description = "根据订单号获取订单详情")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderByNumber(
            @Parameter(description = "订单号") @PathVariable String orderNumber) {
        Optional<OrderDto> order = orderService.getOrderByNumber(orderNumber);
        if (order.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(order.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "获取订单列表", description = "分页获取订单列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getAllOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "bookingDate") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderDto> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的订单", description = "获取当前用户的订单列表")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getMyOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        
        Page<OrderDto> orders = orderService.getOrdersByUser(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取订单", description = "根据订单状态获取订单列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getOrdersByStatus(
            @Parameter(description = "订单状态") @PathVariable Order.Status status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        Page<OrderDto> orders = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/payment-status/{paymentStatus}")
    @Operation(summary = "根据支付状态获取订单", description = "根据支付状态获取订单列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getOrdersByPaymentStatus(
            @Parameter(description = "支付状态") @PathVariable Order.PaymentStatus paymentStatus,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        Page<OrderDto> orders = orderService.getOrdersByPaymentStatus(paymentStatus, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索订单", description = "根据关键词搜索订单")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> searchOrders(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        Page<OrderDto> orders = orderService.searchOrders(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新订单信息", description = "更新订单信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Valid @RequestBody OrderDto orderDto) {
        OrderDto updatedOrder = orderService.updateOrder(id, orderDto);
        return ResponseEntity.ok(ApiResponse.success("订单更新成功", updatedOrder));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam Order.Status status) {
        OrderDto updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("状态更新成功", updatedOrder));
    }

    @PutMapping("/{id}/payment-status")
    @Operation(summary = "更新支付状态", description = "更新订单支付状态")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> updatePaymentStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "新支付状态") @RequestParam Order.PaymentStatus paymentStatus) {
        OrderDto updatedOrder = orderService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(ApiResponse.success("支付状态更新成功", updatedOrder));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单", description = "处理订单支付")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "支付方式") @RequestParam Order.PaymentMethod paymentMethod) {
        OrderDto paidOrder = orderService.processPayment(id, paymentMethod);
        return ResponseEntity.ok(ApiResponse.success("支付成功", paidOrder));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消订单")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("订单取消成功"));
    }

    @GetMapping("/reports/date-range")
    @Operation(summary = "获取日期范围内的订单", description = "获取指定日期范围内的订单")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByDateRange(
            @Parameter(description = "开始日期") @RequestParam 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "结束日期") @RequestParam 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        List<OrderDto> orders = orderService.getOrdersBetweenDates(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "统计订单数量", description = "统计指定状态的订单数量")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> countOrdersByStatus(
            @Parameter(description = "订单状态") @PathVariable Order.Status status) {
        long count = orderService.countOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/payment-status/{paymentStatus}")
    @Operation(summary = "统计支付状态订单数量", description = "统计指定支付状态的订单数量")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> countOrdersByPaymentStatus(
            @Parameter(description = "支付状态") @PathVariable Order.PaymentStatus paymentStatus) {
        long count = orderService.countOrdersByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PostMapping("/cleanup-expired")
    @Operation(summary = "清理过期订单", description = "清理过期的待处理订单")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cleanupExpiredOrders() {
        orderService.cleanupExpiredOrders();
        return ResponseEntity.ok(ApiResponse.success("过期订单清理完成"));
    }
}