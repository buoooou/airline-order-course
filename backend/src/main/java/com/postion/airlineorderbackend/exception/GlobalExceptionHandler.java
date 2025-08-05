package com.postion.airlineorderbackend.exception;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 
 * 统一处理所有控制器抛出的异常，提供标准化的错误响应
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 创建错误响应实体
     * 
     * @param message 错误消息
     * @param status  HTTP状态码
     * @return 错误响应实体
     */
    public static ResponseEntity<ApiResponse<?>> errorResponseEntity(String message, HttpStatus status) {
        ApiResponse<?> response = ApiResponse.error(message);
        return new ResponseEntity<>(response, status);
    }

    /**
     * 处理业务异常
     * 
     * @param e       业务异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常 - 错误码: {}, 消息: {}, 路径: {}", e.getCode(), e.getMessage(), request.getRequestURI(), e);
        return errorResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数校验异常
     * 
     * @param e 参数校验异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("参数校验异常 - 路径: {}", request.getRequestURI(), e);
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // 将错误信息Map转换为字符串消息
        StringBuilder errorMessage = new StringBuilder(Constants.ErrorMessage.VALIDATION_FAILED + ": ");
        errors.forEach((field, message) -> {
            errorMessage.append("[").append(field).append(": ").append(message).append("] ");
        });
        
        return errorResponseEntity(errorMessage.toString().trim(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理404异常
     * 
     * @param e       404异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoHandlerFoundException(NoHandlerFoundException e,
            HttpServletRequest request) {
        log.error("404异常 - 路径: {}", request.getRequestURI(), e);
        return errorResponseEntity(Constants.HttpStatusMessage.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常", e);
        return errorResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理空指针异常
     * 
     * @param e       空指针异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(NullPointerException e,
            HttpServletRequest request) {
        log.error("空指针异常 - 路径: {}", request.getRequestURI(), e);
        return errorResponseEntity(Constants.HttpStatusMessage.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理用户名未找到异常
     * 
     * @param e       用户名未找到异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUsernameNotFoundException(
            org.springframework.security.core.userdetails.UsernameNotFoundException e, HttpServletRequest request) {
        log.error("用户名未找到异常 - 路径: {}", request.getRequestURI(), e);
        return errorResponseEntity("用户名或密码错误", HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理所有未捕获的异常
     * 
     * @param e       异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllExceptions(Exception e, HttpServletRequest request) {
        log.error("未知异常 - 路径: {}", request.getRequestURI(), e);
        return errorResponseEntity("服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}