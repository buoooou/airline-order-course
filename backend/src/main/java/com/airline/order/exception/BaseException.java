package com.airline.order.exception;

/**
 * 基础异常类
 */
public class BaseException extends RuntimeException {
    
    private Integer code;
    
    /**
     * 默认构造函数
     */
    public BaseException() {
        super();
    }
    
    /**
     * 带消息的构造函数
     * 
     * @param message 错误消息
     */
    public BaseException(String message) {
        super(message);
        this.code = 500;
    }
    
    /**
     * 带错误码和消息的构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 带消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
    
    /**
     * 带错误码、消息和原因的构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原因
     */
    public BaseException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }
    
    /**
     * 设置错误码
     * 
     * @param code 错误码
     */
    public void setCode(Integer code) {
        this.code = code;
    }
}