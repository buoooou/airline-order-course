package com.airline.order.exception;

/**
 * 未授权异常
 */
public class UnauthorizedException extends BaseException {
    
    /**
     * 默认构造函数
     */
    public UnauthorizedException() {
        super(401, "未授权访问");
    }
    
    /**
     * 带消息的构造函数
     * 
     * @param message 错误消息
     */
    public UnauthorizedException(String message) {
        super(401, message);
    }
    
    /**
     * 带消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(401, message, cause);
    }
}