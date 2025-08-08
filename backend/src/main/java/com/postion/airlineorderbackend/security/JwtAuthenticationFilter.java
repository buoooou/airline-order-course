package com.postion.airlineorderbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postion.airlineorderbackend.dto.ErrorResponse;
import com.postion.airlineorderbackend.service.CustomUserDetailsService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*过滤器，拦截请求，从请求头中解析出 token。
校验 token，设置认证上下文（SecurityContextHolder）。*/
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;
        try {
			if (header != null && header.startsWith("Bearer ")) {
			    token = header.substring(7);
			    if (jwtTokenProvider.validateToken(token)) {
			        username = jwtTokenProvider.getUsernameFromToken(token);
			    }else {
			    	// token 无效，手动写入响应
	                writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Your session has expired. Please login again.", "TOKEN_EXPIRED");
	                return; // 不继续传递过滤器链
			    }
			}

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
			    UsernamePasswordAuthenticationToken authToken =
			            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			    SecurityContextHolder.getContext().setAuthentication(authToken);
			}

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			writeErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error occurred", "UNEXPECTED_ERROR");
		} 	
    }
    
	/*
	 * GlobalExceptionHandler 处理的是 控制器方法中抛出的异常，不适用于 Filter 中 解决方法：Filter
	 * 中手动写入响应并终止处理链。
	 */
    private void writeErrorResponse(HttpServletResponse response, int status, String message, String errorCode) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse error = ErrorResponse.builder()
                .status(status)
                .error(errorCode) // 这里填 TOKEN_EXPIRED 或其他
                .message(message)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        new ObjectMapper().writeValue(response.getWriter(), error);
    }

}
