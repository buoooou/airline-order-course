package com.position.airlineorderbackend.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TicketingSystemException extends RuntimeException {
    
    private final String errorCode;
    
    public TicketingSystemException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public TicketingSystemException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
} 