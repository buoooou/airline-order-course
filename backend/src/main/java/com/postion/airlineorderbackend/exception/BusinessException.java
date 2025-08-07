package com.postion.airlineorderbackend.exception;
import lombok.Data;

import org.springframework.http.HttpStatus;

/**
 * BusinessException
 */
@Data
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;  // 可选：自定义错误码

    /**
     * BusinessException
     *
     * @param status
     * @param message
     */
    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = null;
    }

    /**
     * BusinessException
     *
     * @param errorCode
     * @param message
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST; // 默认400错误
        this.errorCode = errorCode;
    }

    /**
     * BusinessException
     *
     * @param httpStatus
     * @param errorCode
     * @param message
     */
    public BusinessException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.status = httpStatus;
        this.errorCode = errorCode;
    }


}