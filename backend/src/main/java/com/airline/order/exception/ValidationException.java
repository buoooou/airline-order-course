package com.airline.order.exception;

/**
 * 参数验证异常
 */
public class ValidationException extends BaseException {
    
    /**
     * 默认构造函数
     */
    public ValidationException() {
        super(400, "参数验证失败");
    }
    
    /**
     * 带消息的构造函数
     * 
     * @param message 错误消息
     */
    public ValidationException(String message) {
        super(400, message);
    }
    
    /**
     * 带消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public ValidationException(String message, Throwable cause) {
        super(400, message, cause);
    }
}