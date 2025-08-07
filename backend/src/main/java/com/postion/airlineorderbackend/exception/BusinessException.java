package com.postion.airlineorderbackend.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误代码
     */
    private final String errorCode;
    
    /**
     * HTTP状态码
     */
    private final int httpStatus;
    
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = 400;
    }
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }
    
    public BusinessException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = 400;
    }
    
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }
    
    // 常用的业务异常静态方法
    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, "UNAUTHORIZED", 401);
    }
    
    public static BusinessException forbidden(String message) {
        return new BusinessException(message, "PERMISSION_DENIED", 403);
    }
    
    public static BusinessException notFound(String message) {
        return new BusinessException(message, "NOT_FOUND", 404);
    }
    
    public static BusinessException badRequest(String message) {
        return new BusinessException(message, "BAD_REQUEST", 400);
    }
    
    public static BusinessException invalidStatus(String message) {
        return new BusinessException(message, "INVALID_STATUS", 400);
    }
    
    public static BusinessException orderNotFound() {
        return new BusinessException("订单不存在", "ORDER_NOT_FOUND", 404);
    }
    
    public static BusinessException flightNotFound() {
        return new BusinessException("航班不存在", "FLIGHT_NOT_FOUND", 404);
    }
    
    public static BusinessException userNotFound() {
        return new BusinessException("用户不存在", "USER_NOT_FOUND", 404);
    }
}
