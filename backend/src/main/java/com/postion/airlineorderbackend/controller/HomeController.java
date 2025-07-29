package com.postion.airlineorderbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 首页控制器
 */
@RestController
public class HomeController {

    /**
     * 根路径重定向到Swagger UI
     */
    @GetMapping("/")
    public void redirectToSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
} 