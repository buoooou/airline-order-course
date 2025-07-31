package com.postion.airlineorderbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/test")
public class TestController{
    
    @GetMapping()
    @PostMapping("/")
    public String getAllOrders() {
        return "orderService.getAllOrders()";
    }

    @PostMapping("/pay")
    public String createOrder(@RequestParam String param) {
        return "orderService.payOrder()";
    }

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam String param) {
        return "orderService.cancelOrder()";
    }
}