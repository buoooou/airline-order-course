package com.postion.airlineorderbackend.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.postion.airlineorderbackend.service.JwtService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器，用于拦截请求并验证JWT令牌
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Resource
    private JwtService jwtService;

    @Resource
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求头中获取Authorization字段
            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;

            // 检查Authorization头是否存在且以Bearer开头
            if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
                filterChain.doFilter(request, response);
                return;
            }

            // 提取JWT令牌（去掉"Bearer "前缀）
            jwt = authorizationHeader.substring(7);
            // 从JWT令牌中提取用户名
            username = jwtService.extractUsername(jwt);

            // 如果用户名不为空，且当前SecurityContext中没有认证信息
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户详情
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 验证令牌是否有效
                if (jwtService.isTokenVaild(jwt, userDetails)) {
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    
                    // 设置认证详情
                    usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 将认证信息存入SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            // 继续执行过滤链
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("无法设置用户认证: {}", e);
        }

    }
}
    