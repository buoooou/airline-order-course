package com.postion.airlineorderbackend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlingFilter extends org.springframework.web.filter.OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(response, request.getRequestURI(), ex);
        }
    }

    private void handleException(HttpServletResponse response, String path, Exception ex) throws IOException {
        if (response.isCommitted()) {
            return; // 已经写出响应
        }
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = "INTERNAL_ERROR";
        String message = "服务器内部错误";
        Map<String, Object> details = null;

        if (ex instanceof BusinessException) {
            status = HttpStatus.BAD_REQUEST;
            code = ((BusinessException) ex).getErrorCode() != null ? ((BusinessException) ex).getErrorCode() : "BUSINESS_ERROR";
            message = ex.getMessage();
        } else if (ex instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            code = "NOT_FOUND";
            message = ex.getMessage();
        } else if (ex instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
            code = "ACCESS_DENIED";
            message = ex.getMessage();
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            status = HttpStatus.METHOD_NOT_ALLOWED;
            code = "METHOD_NOT_ALLOWED";
            message = ex.getMessage();
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            code = "UNSUPPORTED_MEDIA_TYPE";
            message = ex.getMessage();
        } else if (ex instanceof DataIntegrityViolationException) {
            status = HttpStatus.CONFLICT;
            code = "DATA_INTEGRITY_VIOLATION";
            message = "数据约束冲突";
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            code = "ILLEGAL_ARGUMENT";
            message = ex.getMessage();
        }

        ApiErrorResponse body = new ApiErrorResponse(code, message, status.value(), path);
        body.setDetails(details);

        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
