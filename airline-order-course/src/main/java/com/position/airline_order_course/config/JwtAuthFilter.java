package com.position.airline_order_course.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.position.airline_order_course.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JWT鉴权过滤器（HTTP请求时检查请求头中的JWT Token）
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 如果已认证就跳过
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
            String token = authHeader.substring(7);
            String username = null;

            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.warn("无法解析JWT中的用户名", e);
                filterChain.doFilter(request, response);
                return;
            }

            // 加载用户并验证Token
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // 从数据库加载用户
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 验证token是否有效
                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        logger.warn("JWT token验证失败");
                    }
                } catch (Exception e) {
                    logger.warn("加载用户失败: " + username, e);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}