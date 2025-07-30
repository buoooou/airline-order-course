package com.postion.airlineorderbackend.Exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AirlineApiClientException extends RuntimeException {

    private final HttpStatus status;

    public AirlineApiClientException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AirlineApiClientException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
