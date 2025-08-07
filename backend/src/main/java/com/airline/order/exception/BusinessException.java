package com.airline.order.exception;

/**
 * 业务异常类
 */
public class BusinessException extends BaseException {
    
    /**
     * 默认构造函数
     */
    public BusinessException() {
        super();
    }
    
    /**
     * 带消息的构造函数
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * 带错误码和消息的构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(code, message);
    }
    
    /**
     * 带消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 带错误码、消息和原因的构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
}