package com.position.airline_order_course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.position.airline_order_course.dto.TicketResponse;
import com.position.airline_order_course.service.MockTicketService;
import jakarta.validation.constraints.NotBlank;

/*
 * 出票服务Controller
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    private MockTicketService ticketService;

    /*
     * 提交一个出票任务
     */
    @PostMapping("/issue")
    public TicketResponse<String> issueTicket(@RequestParam @NotBlank String orderId) {
        return ticketService.issueTicket(orderId);
    }

    /*
     * 获取所有待处理的票
     */
    @GetMapping("/pending")
    public TicketResponse<List<String>> getPendingTasks() {
        return ticketService.getPendingTasks();
    }

    /*
     * 重试处理之前失败的任务
     */
    @PostMapping("/retry")
    public TicketResponse<String> retryTicket(@RequestParam @NotBlank String orderId) {
        return ticketService.retryTicket(orderId);
    }
}
