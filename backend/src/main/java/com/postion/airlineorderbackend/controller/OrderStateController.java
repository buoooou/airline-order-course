package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.StateTransitionRequest;
import com.postion.airlineorderbackend.entity.AppUser;
import com.postion.airlineorderbackend.repository.AppUserRepository;
import com.postion.airlineorderbackend.statemachine.OrderEvent;
import com.postion.airlineorderbackend.statemachine.OrderStateContext;
import com.postion.airlineorderbackend.statemachine.OrderStateMachineService;
import com.postion.airlineorderbackend.statemachine.OrderStateMachineUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单状态机控制器
 * 提供状态查询和操作的REST API接口
 */
@RestController
@RequestMapping("/api/orders/state")
@RequiredArgsConstructor
@Slf4j
public class OrderStateController {

    private final OrderStateMachineService stateMachineService;
    private final OrderStateMachineUtil stateMachineUtil;
    private final AppUserRepository appUserRepository;

    /**
     * 获取订单状态详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderState(@PathVariable Long orderId) {
        try {
            Map<String, Object> stateDetail = stateMachineUtil.getOrderStateDetail(orderId);
            if (stateDetail.containsKey("error")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error((String) stateDetail.get("error")));
            }
            return ResponseEntity.ok(ApiResponse.success("获取订单状态成功", stateDetail));
        } catch (Exception e) {
            log.error("获取订单状态失败: {}", orderId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取订单状态失败: " + e.getMessage()));
        }
    }

    /**
     * 获取订单允许的触发事件
     */
    @GetMapping("/{orderId}/allowed-events")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getAllowedEvents(@PathVariable Long orderId) {
        try {
            List<Map<String, String>> allowedEvents = stateMachineUtil.getAllowedEvents(orderId);
            return ResponseEntity.ok(ApiResponse.success("获取允许事件成功", allowedEvents));
        } catch (Exception e) {
            log.error("获取允许事件失败: {}", orderId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取允许事件失败: " + e.getMessage()));
        }
    }

    /**
     * 触发订单状态转换
     */
    @PostMapping("/{orderId}/trigger")
    public ResponseEntity<ApiResponse<Map<String, Object>>> triggerStateTransition(
            @PathVariable Long orderId,
            @RequestBody StateTransitionRequest request) {
        try {
            // 验证事件参数
            if (request.event() == null || request.event().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("事件不能为空"));
            }

            OrderEvent event;
            try {
                event = OrderEvent.valueOf(request.event().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("无效的事件: " + request.event()));
            }

            // 获取当前认证用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户未认证"));
            }

            String currentUser = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            // 根据用户名查找用户ID
            AppUser user = appUserRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

            // 验证是否可以触发事件
            if (!stateMachineService.canTriggerEvent(orderId, event)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("当前状态不允许执行此事件"));
            }

            // 创建状态上下文
            OrderStateContext context = new OrderStateContext();
            context.setOrderId(orderId);
            context.setOperator(currentUser);
            context.setRemark(request.remark() != null ? request.remark() : "");

            // 触发状态转换，传递用户信息进行权限验证
            String role = isAdmin ? "admin" : "user";
            boolean success = stateMachineService.triggerStateTransition(orderId, event, context, user.getId(), role);

            if (success) {
                Map<String, Object> result = Map.of(
                        "orderId", orderId,
                        "event", event.name(),
                        "success", true,
                        "message", "状态转换成功");
                return ResponseEntity.ok(ApiResponse.success("状态转换成功", result));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("状态转换失败"));
            }

        } catch (Exception e) {
            log.error("状态转换异常: 订单={}, 事件={}", orderId, request.event(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("状态转换异常: " + e.getMessage()));
        }
    }

    /**
     * 获取所有状态定义
     */
    @GetMapping("/states")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getAllStates() {
        try {
            List<Map<String, String>> states = stateMachineUtil.getAllStates();
            return ResponseEntity.ok(ApiResponse.success("获取状态列表成功", states));
        } catch (Exception e) {
            log.error("获取状态列表失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取状态列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有事件定义
     */
    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getAllEvents() {
        try {
            List<Map<String, String>> events = stateMachineUtil.getAllEvents();
            return ResponseEntity.ok(ApiResponse.success("获取事件列表成功", events));
        } catch (Exception e) {
            log.error("获取事件列表失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取事件列表失败: " + e.getMessage()));
        }
    }

}