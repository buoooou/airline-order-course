package com.airline.order.exception;

/**
 * 资源未找到异常
 */
public class ResourceNotFoundException extends BaseException {
    
    /**
     * 默认构造函数
     */
    public ResourceNotFoundException() {
        super(404, "资源不存在");
    }
    
    /**
     * 带消息的构造函数
     * 
     * @param message 错误消息
     */
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
    
    /**
     * 带资源类型和ID的构造函数
     * 
     * @param resourceName 资源名称
     * @param id 资源ID
     */
    public ResourceNotFoundException(String resourceName, Object id) {
        super(404, String.format("%s不存在，ID: %s", resourceName, id));
    }
}