package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 安全配置类
 * 配置HTTP安全规则、JWT过滤器、CORS等
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    
    private final JwtUtil jwtUtil;
    private final JwtRequestFilter jwtRequestFilter;
    
    /**
     * 密码编码器Bean
     * 使用BCrypt加密算法
     * 
     * @return BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 认证管理器Bean
     * 用于用户认证
     * 
     * @param authConfig 认证配置
     * @return 认证管理器
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    /**
     * CORS配置源Bean
     * 配置跨域资源共享规则
     * 
     * @return CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源（前端地址）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", 
                "Accept", "Origin", "Access-Control-Request-Method", 
                "Access-Control-Request-Headers"
        ));
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * 安全过滤器链配置
     * 配置HTTP安全规则
     * 
     * @param http HttpSecurity对象
     * @return 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("配置Spring Security安全过滤器链");
        
        http
            // 禁用CSRF保护（因为使用JWT，不需要CSRF保护）
            .csrf().disable()
            
            // 配置CORS
            .cors().configurationSource(corsConfigurationSource())
            
            .and()
            
            // 配置会话管理策略为无状态（因为使用JWT）
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            .and()
            
            // 配置授权规则
            .authorizeRequests(authz -> authz
                // 公开访问的端点（不需要认证）
                .antMatchers(
                    "/api/auth/login",           // 登录接口
                    "/api/auth/register",        // 注册接口
                    "/api/flights",              // 查看航班列表（公开）
                    "/api/flights/*",            // 查看航班详情（公开）
                    "/api/flights/search/**",    // 搜索航班（公开）
                    "/api/flights/bookable",     // 可预订航班（公开）
                    "/swagger-ui/**",            // Swagger UI
                    "/swagger-ui.html",          // Swagger UI首页
                    "/swagger-ui/index.html",    // Swagger UI主页
                    "/v3/api-docs/**",           // OpenAPI文档
                    "/api-docs/**",              // API文档（兼容路径）
                    "/swagger-resources/**",     // Swagger资源
                    "/webjars/**",               // Web资源
                    "/favicon.ico",              // 网站图标
                    "/error",                    // 错误页面
                    "/actuator/**"               // Spring Boot Actuator（如果启用）
                ).permitAll()
                
                // 需要认证的端点
                .antMatchers(
                    "/api/auth/validate",        // 验证令牌
                    "/api/auth/refresh",         // 刷新令牌
                    "/api/auth/logout",          // 登出
                    "/api/auth/me",              // 获取当前用户信息
                    "/api/auth/token/check",     // 检查令牌状态
                    "/api/orders/**"             // 所有订单相关接口
                ).authenticated()
                
                // 管理员权限的端点
                .antMatchers(
                    "/api/orders/search",        // 搜索所有订单
                    "/api/orders/statistics"     // 订单统计
                ).hasRole("ADMIN")
                
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            
            // 添加JWT过滤器
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 配置异常处理
            .exceptionHandling(exceptions -> exceptions
                // 认证失败处理
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("认证失败: {} - {}", request.getRequestURI(), authException.getMessage());
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"success\":false,\"message\":\"未认证或令牌无效\",\"error\":\"UNAUTHORIZED\"}"
                    );
                })
                
                // 授权失败处理
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("授权失败: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"success\":false,\"message\":\"权限不足\",\"error\":\"ACCESS_DENIED\"}"
                    );
                })
            );
        
        log.info("Spring Security安全过滤器链配置完成");
        return http.build();
    }
}
