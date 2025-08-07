package com.airline.order.exception;

/**
 * 异常工具类
 */
public class ExceptionUtils {
    
    /**
     * 抛出业务异常
     * 
     * @param message 错误消息
     */
    public static void throwBusinessException(String message) {
        throw new BusinessException(message);
    }
    
    /**
     * 抛出业务异常
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public static void throwBusinessException(Integer code, String message) {
        throw new BusinessException(code, message);
    }
    
    /**
     * 抛出业务异常
     * 
     * @param errorCode 错误码枚举
     */
    public static void throwBusinessException(ErrorCode errorCode) {
        throw new BusinessException(errorCode.getCode(), errorCode.getMessage());
    }
    
    /**
     * 抛出资源未找到异常
     * 
     * @param message 错误消息
     */
    public static void throwResourceNotFoundException(String message) {
        throw new ResourceNotFoundException(message);
    }
    
    /**
     * 抛出资源未找到异常
     * 
     * @param resourceName 资源名称
     * @param id 资源ID
     */
    public static void throwResourceNotFoundException(String resourceName, Object id) {
        throw new ResourceNotFoundException(resourceName, id);
    }
    
    /**
     * 抛出未授权异常
     * 
     * @param message 错误消息
     */
    public static void throwUnauthorizedException(String message) {
        throw new UnauthorizedException(message);
    }
    
    /**
     * 抛出参数验证异常
     * 
     * @param message 错误消息
     */
    public static void throwValidationException(String message) {
        throw new ValidationException(message);
    }
    
    /**
     * 根据条件抛出业务异常
     * 
     * @param condition 条件
     * @param message 错误消息
     */
    public static void throwIf(boolean condition, String message) {
        if (condition) {
            throwBusinessException(message);
        }
    }
    
    /**
     * 根据条件抛出业务异常
     * 
     * @param condition 条件
     * @param errorCode 错误码枚举
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throwBusinessException(errorCode);
        }
    }
    
    /**
     * 根据条件抛出业务异常
     * 
     * @param condition 条件
     * @param code 错误码
     * @param message 错误消息
     */
    public static void throwIf(boolean condition, Integer code, String message) {
        if (condition) {
            throwBusinessException(code, message);
        }
    }
    
    /**
     * 对象为空时抛出资源未找到异常
     * 
     * @param object 对象
     * @param message 错误消息
     */
    public static void throwIfNull(Object object, String message) {
        if (object == null) {
            throwResourceNotFoundException(message);
        }
    }
    
    /**
     * 对象为空时抛出资源未找到异常
     * 
     * @param object 对象
     * @param resourceName 资源名称
     * @param id 资源ID
     */
    public static void throwIfNull(Object object, String resourceName, Object id) {
        if (object == null) {
            throwResourceNotFoundException(resourceName, id);
        }
    }
}