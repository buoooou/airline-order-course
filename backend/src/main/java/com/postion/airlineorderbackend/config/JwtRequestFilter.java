package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT请求过滤器
 * 拦截HTTP请求，验证JWT令牌并设置安全上下文
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    /**
     * 过滤器核心方法
     * 从请求头中提取JWT令牌，验证并设置安全上下文
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        
        String username = null;
        String jwtToken = null;
        
        // 检查Authorization头部是否存在且格式正确
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7); // 移除"Bearer "前缀
            
            try {
                // 从JWT令牌中提取用户名
                username = jwtUtil.getUsernameFromToken(jwtToken);
                log.debug("从JWT令牌中提取用户名: {} - URI: {}", username, requestURI);
                
            } catch (Exception e) {
                log.warn("JWT令牌解析失败: {} - URI: {}", e.getMessage(), requestURI);
            }
        } else {
            log.debug("请求未包含有效的Authorization头部 - URI: {}", requestURI);
        }
        
        // 如果成功提取用户名且当前安全上下文中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            try {
                // 验证JWT令牌
                if (jwtUtil.validateToken(jwtToken)) {
                    log.debug("JWT令牌验证成功: {} - URI: {}", username, requestURI);
                    
                    // 从JWT令牌中提取用户角色
                    String role = jwtUtil.getRoleFromToken(jwtToken);
                    
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                    
                    // 设置认证详情
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 将认证信息设置到安全上下文中
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("安全上下文设置成功: {} (角色: {}) - URI: {}", username, role, requestURI);
                    
                } else {
                    log.warn("JWT令牌验证失败: {} - URI: {}", username, requestURI);
                }
                
            } catch (Exception e) {
                log.error("JWT令牌处理异常: {} - URI: {}", e.getMessage(), requestURI, e);
            }
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
    
    /**
     * 判断是否应该跳过过滤
     * 对于某些特定路径可以跳过JWT验证
     * 
     * @param request HTTP请求
     * @return 是否跳过过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 跳过公开访问的路径
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/register") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/error") ||
               path.startsWith("/actuator") ||
               (path.startsWith("/api/flights") && "GET".equals(request.getMethod()));
    }
}
