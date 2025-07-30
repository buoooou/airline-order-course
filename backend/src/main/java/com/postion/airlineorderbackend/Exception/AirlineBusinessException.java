package com.postion.airlineorderbackend.Exception;

public class AirlineBusinessException extends RuntimeException {

    public AirlineBusinessException(String message) {
        super(message);
    }

    public AirlineBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
