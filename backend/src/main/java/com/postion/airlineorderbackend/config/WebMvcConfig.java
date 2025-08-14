package com.postion.airlineorderbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 配置拦截器、跨域等Web相关设置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    /**
     * 添加请求日志拦截器
     * 拦截所有请求路径
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**"); // 排除Swagger相关路径
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 关键：添加静态资源映射（若没有此配置，默认已包含，但自定义时需显式添加）
        registry.addResourceHandler("/**") // 映射所有路径
                .addResourceLocations("classpath:/static/") // 静态资源所在目录
                .addResourceLocations("classpath:/public/"); // 可选：其他资源目录
    }
}