package com.postion.airlineorderbackend.configuration;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.postion.airlineorderbackend.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JwtAuthFilter# doFilterInternal start.");
        System.out.println();
        System.out.println();
        System.out.println("JwtAuthFilter# doFilterInternal start.");

        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.info("JwtAuthFilter# request header do not include Authorization or Bearer.");
            System.out.println("JwtAuthFilter# request header do not include Authorization or Bearer.");

            filterChain.doFilter(request, response);
            return;
        }
        final String token = header.substring(7);
        final String username = jwtUtil.extractUsername(token);
        log.debug("JwtAuthFilter# Token:{}, extracted username:{}", token, username);
        System.out.println("JwtAuthFilter# Token:" + token + ", extracted username:" + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.debug("JwtAuthFilter# UserDetails.username:{}, UserDetails.password:{}", userDetails.getUsername(), userDetails.getPassword());
            System.out.println("JwtAuthFilter# UserDetails.username:" + userDetails.getUsername() + ", UserDetails.password:" + userDetails.getPassword());

            if (jwtUtil.validateToken(token, userDetails)) {
                // 创建认证令牌并设置到安全上下文
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("JwtAuthFilter# JWT令牌验证失败: {} - URI: {}", username, request.getRequestURI());
                System.out.println("JwtAuthFilter# JWT authentication failed.");
            }
        }
        filterChain.doFilter(request, response);
    }

}
