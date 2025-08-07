package com.postion.airlineorderbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private String resourceName; // 可选：资源名称（如"订单"）
    private String fieldName;    // 可选：字段名称（如"id"）
    private Object fieldValue;  // 可选：字段值

    // 通用业务异常构造器
    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // 资源不存在场景专用构造器（自动设置404状态）
    public BusinessException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.status = HttpStatus.NOT_FOUND;
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
