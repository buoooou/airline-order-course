package com.airline.order.exception;

/**
 * 错误码枚举
 */
public enum ErrorCode {
    
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统错误"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权访问"),
    
    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 方法不允许
     */
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    
    /**
     * 业务错误
     */
    BUSINESS_ERROR(600, "业务错误"),
    
    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(601, "用户名或密码错误"),
    
    /**
     * 令牌无效
     */
    INVALID_TOKEN(602, "令牌无效"),
    
    /**
     * 令牌过期
     */
    TOKEN_EXPIRED(603, "令牌已过期");
    
    private final Integer code;
    private final String message;
    
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
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
     * 获取错误消息
     * 
     * @return 错误消息
     */
    public String getMessage() {
        return message;
    }
}