package com.postion.airlineorderbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HTTP请求日志拦截器
 * 记录所有进入的HTTP请求的详细信息
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "requestStartTime";

    /**
     * 请求处理前记录日志
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);
        
        log.info("[HTTP REQUEST] {} {} from {} - Handler: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                getClientIpAddress(request),
                handler.getClass().getSimpleName());
        
        log.debug("[HTTP REQUEST DETAILS] Query: {} | Headers: {} | Content-Type: {}", 
                request.getQueryString(),
                request.getHeader("User-Agent"),
                request.getContentType());
        
        return true;
    }

    /**
     * 请求处理后记录响应信息
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[HTTP RESPONSE] {} {} - Status: {} - Duration: {}ms", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    response.getStatus(),
                    duration);
        }
    }

    /**
     * 请求完成后记录最终信息
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            log.error("[HTTP ERROR] {} {} - Exception: {}", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    ex.getMessage(), 
                    ex);
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}