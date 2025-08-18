package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.service.JwtService;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("========== JwtAuthenticationFilter called");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 判断路径，如果是不需要登录的路径，则直接放行
        // 其余的路径，需要登录
        String uri = request.getRequestURI();
        log.info("========== JwtAuthenticationFilter called for path: {}", uri);

        // login/register/swagger-ui/v2/api-docs的场合，直接放行
        if (uri.equals("/") ||
                uri.equals("/index.html") ||
                uri.startsWith("/static/") ||
                uri.startsWith("/assets/") ||
                uri.endsWith(".js") ||
                uri.endsWith(".css") ||
                uri.endsWith(".ico") ||
                uri.endsWith(".png") ||
                uri.contains("/login") ||
                uri.contains("/register") ||
                uri.contains("/swagger") ||
                uri.contains("/actuator/health") ||
                uri.contains("/api-docs")) {
            log.info("========== JwtAuthenticationFilter skip for path: {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        // 请求头中没有Authorization字段，则抛出401错误
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("authHeader:" + authHeader);
            log.error("========== JWT token not found in request!");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid headers!");
            return;
        }

        // 解析JWT token
        jwt = authHeader.substring(7);
        try {
            username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("========== JwtAuthenticationFilter found JWT token for user: {}", username);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("========== JwtAuthenticationFilter set user authentication: {}", authToken);
                } else {
                    log.error("========== JWT token is not valid!");
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e);
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token");
            return;
        }

        log.info("========== JwtAuthenticationFilter ended");
        filterChain.doFilter(request, response);
    }
}