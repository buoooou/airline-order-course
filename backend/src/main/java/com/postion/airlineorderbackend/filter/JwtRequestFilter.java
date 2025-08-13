package com.postion.airlineorderbackend.filter;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.postion.airlineorderbackend.util.JwtUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. 从请求头获取Token
        final String authorizationHeader = request.getHeader("Authorization");
        
        // 2. 验证Token格式
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // 公开接口放行
            if (request.getRequestURI().startsWith("/api/public/") || 
                request.getRequestURI().startsWith("/swagger-ui.html") || 
                request.getRequestURI().startsWith("/swagger-ui/") || 
                request.getRequestURI().startsWith("/v3/api-docs") || 
                request.getRequestURI().startsWith("/v3/api-docs/") || 
                request.getRequestURI().startsWith("/webjars/") || 
                request.getRequestURI().startsWith("/swagger-resources/") || 
                request.getRequestURI().startsWith("/favicon.ico") || 
                request.getRequestURI().startsWith("/api/auth/") || 
                request.getRequestURI().startsWith("/actuator/")) {
                filterChain.doFilter(request, response);
                return;
            }
            // 非公开接口拒绝
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权的访问");
            return;
        }
        
        // 3. 提取并验证Token
        String jwt = authorizationHeader.substring(7);
        if (jwt == null || jwt.trim().isEmpty()) {
            log.warn("Empty or invalid JWT token");
            // 公开接口放行
            if (request.getRequestURI().startsWith("/api/public/") || 
                request.getRequestURI().startsWith("/swagger-ui.html") || 
                request.getRequestURI().startsWith("/swagger-ui/") || 
                request.getRequestURI().startsWith("/v3/api-docs") || 
                request.getRequestURI().startsWith("/v3/api-docs/") || 
                request.getRequestURI().startsWith("/webjars/") || 
                request.getRequestURI().startsWith("/swagger-resources/") || 
                request.getRequestURI().startsWith("/favicon.ico") || 
                request.getRequestURI().startsWith("/api/auth/") || 
                request.getRequestURI().startsWith("/actuator/")) {
                filterChain.doFilter(request, response);
                return;
            }
            // 非公开接口拒绝
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权的访问");
            return;
        }
        
        try {
            String username = jwtUtil.extractUsername(jwt);
            if (username == null || username.isEmpty()) {
                throw new BadCredentialsException("Username cannot be null or empty");
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 显式检查用户是否存在
            if (userDetails == null) {
                log.warn("JWT验证失败: username={}, 原因=用户不存在", username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户不存在或账户已禁用");
                return;
            }
            
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            // Consider caching the validated token to avoid repeated validation
        } catch (JwtException e) {
            log.error("Token validation failed", e);
            throw new AuthenticationCredentialsNotFoundException("Invalid or expired token");
        } catch (Exception e) {
            log.error("Unexpected error during token validation", e);
            throw new AuthenticationServiceException("Internal server error");
        }
        
        // 4. 继续过滤器链
        filterChain.doFilter(request, response);
    }
}