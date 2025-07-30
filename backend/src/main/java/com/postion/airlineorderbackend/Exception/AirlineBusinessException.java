package com.postion.airlineorderbackend.Exception;

import lombok.Getter;

@Getter
public class AirlineBusinessException extends RuntimeException {

    private final int code;

    public AirlineBusinessException(int errorCode, String message) {
        super(message);
        this.code = errorCode;
    }

    public AirlineBusinessException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }
}
