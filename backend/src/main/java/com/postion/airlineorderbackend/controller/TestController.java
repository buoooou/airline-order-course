package com.postion.airlineorderbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "航空订单后台管理系统启动成功！";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
} 