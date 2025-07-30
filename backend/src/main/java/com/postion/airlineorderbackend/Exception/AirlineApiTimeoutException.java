package com.postion.airlineorderbackend.Exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AirlineApiTimeoutException extends RuntimeException {

    private final HttpStatus status;

    public AirlineApiTimeoutException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AirlineApiTimeoutException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
