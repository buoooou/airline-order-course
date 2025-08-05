package com.position.airlineorderbackend.controller;

import com.position.airlineorderbackend.client.TicketingClient;
import com.position.airlineorderbackend.dto.TicketingRequest;
import com.position.airlineorderbackend.dto.TicketingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/ticketing-test")
@Tag(name = "出票系统测试", description = "Mock出票系统的各种异常场景测试API")
public class TicketingTestController {

    @Autowired
    private TicketingClient ticketingClient;

    @Operation(summary = "测试正常出票", description = "测试出票系统的正常出票流程")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "出票成功", 
                    content = @Content(schema = @Schema(implementation = TicketingResponse.class)))
    })
    @PostMapping("/normal-ticketing")
    public TicketingResponse testNormalTicketing() {
        TicketingRequest request = createSampleRequest("ORD-2024-001");
        return ticketingClient.issueTicket(request);
    }

    @Operation(summary = "测试系统超时异常", description = "测试出票系统超时异常场景")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "503", description = "系统超时异常")
    })
    @PostMapping("/timeout-exception")
    public TicketingResponse testTimeoutException() {
        TicketingRequest request = createSampleRequest("ORD-TIMEOUT-001");
        return ticketingClient.issueTicket(request);
    }

    @Operation(summary = "测试座位不足异常", description = "测试座位不足的业务异常场景")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "返回失败响应", 
                    content = @Content(schema = @Schema(implementation = TicketingResponse.class)))
    })
    @PostMapping("/no-seat-exception")
    public TicketingResponse testNoSeatException() {
        TicketingRequest request = createSampleRequest("ORD-NO_SEAT-001");
        return ticketingClient.issueTicket(request);
    }

    /**
     * 测试航班取消异常
     */
    @PostMapping("/flight-cancelled-exception")
    public TicketingResponse testFlightCancelledException() {
        TicketingRequest request = createSampleRequest("ORD-FLIGHT_CANCELLED-001");
        return ticketingClient.issueTicket(request);
    }

    /**
     * 测试乘客信息错误异常
     */
    @PostMapping("/invalid-passenger-exception")
    public TicketingResponse testInvalidPassengerException() {
        TicketingRequest request = createSampleRequest("ORD-INVALID_PASSENGER-001");
        return ticketingClient.issueTicket(request);
    }

    /**
     * 测试系统维护异常
     */
    @PostMapping("/maintenance-exception")
    public TicketingResponse testMaintenanceException() {
        TicketingRequest request = createSampleRequest("ORD-MAINTENANCE-001");
        return ticketingClient.issueTicket(request);
    }

    /**
     * 测试网络连接失败异常
     */
    @PostMapping("/network-error-exception")
    public TicketingResponse testNetworkErrorException() {
        TicketingRequest request = createSampleRequest("ORD-NETWORK_ERROR-001");
        return ticketingClient.issueTicket(request);
    }

    /**
     * 测试随机失败（10%概率）
     */
    @PostMapping("/random-failure")
    public TicketingResponse testRandomFailure() {
        TicketingRequest request = createSampleRequest("ORD-RANDOM-001");
        return ticketingClient.issueTicket(request);
    }

    /**
     * 测试查询出票状态
     */
    @GetMapping("/query-status/{orderNumber}")
    public TicketingResponse testQueryStatus(@PathVariable String orderNumber) {
        return ticketingClient.queryTicketingStatus(orderNumber);
    }

    /**
     * 测试查询出票状态失败
     */
    @GetMapping("/query-status-failed")
    public TicketingResponse testQueryStatusFailed() {
        return ticketingClient.queryTicketingStatus("ORD-QUERY_FAILED-001");
    }

    /**
     * 测试取消出票
     */
    @PostMapping("/cancel-ticket")
    public TicketingResponse testCancelTicket() {
        return ticketingClient.cancelTicket("TKT123456789", "乘客要求取消");
    }

    /**
     * 测试取消出票失败
     */
    @PostMapping("/cancel-ticket-failed")
    public TicketingResponse testCancelTicketFailed() {
        return ticketingClient.cancelTicket("TKT-CANCEL_FAILED-001", "乘客要求取消");
    }

    /**
     * 测试改签
     */
    @PostMapping("/change-ticket")
    public TicketingResponse testChangeTicket() {
        TicketingRequest newRequest = createSampleRequest("ORD-CHANGE-001");
        return ticketingClient.changeTicket("TKT123456789", newRequest);
    }

    /**
     * 测试改签失败
     */
    @PostMapping("/change-ticket-failed")
    public TicketingResponse testChangeTicketFailed() {
        TicketingRequest newRequest = createSampleRequest("ORD-CHANGE-001");
        return ticketingClient.changeTicket("TKT-CHANGE_FAILED-001", newRequest);
    }

    /**
     * 测试座位可用性查询
     */
    @GetMapping("/seat-availability/{flightNumber}/{seatClass}")
    public boolean testSeatAvailability(@PathVariable String flightNumber, @PathVariable String seatClass) {
        return ticketingClient.checkSeatAvailability(flightNumber, seatClass);
    }

    /**
     * 测试座位查询失败
     */
    @GetMapping("/seat-query-failed")
    public boolean testSeatQueryFailed() {
        return ticketingClient.checkSeatAvailability("CA-SEAT_QUERY_FAILED-1234", "ECONOMY");
    }

    /**
     * 测试座位不足
     */
    @GetMapping("/seat-no-available")
    public boolean testSeatNoAvailable() {
        return ticketingClient.checkSeatAvailability("CA-NO_SEAT-1234", "BUSINESS");
    }

    /**
     * 创建示例出票请求
     */
    private TicketingRequest createSampleRequest(String orderNumber) {
        TicketingRequest request = new TicketingRequest();
        request.setOrderNumber(orderNumber);
        request.setFlightNumber("CA1234");
        request.setPassengerName("张三");
        request.setPassengerId("110101199001011234");
        request.setSeatClass("ECONOMY");
        request.setAmount(new BigDecimal("1500.00"));
        request.setDepartureCity("北京");
        request.setArrivalCity("上海");
        request.setDepartureTime("2024-01-15 10:00:00");
        request.setAirlineCode("CA");
        return request;
    }
} 