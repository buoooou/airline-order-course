package com.postion.airlineorderbackend.exception;

import com.postion.airlineorderbackend.enums.TicketStatusErrorCode;

public class TicketStatusException extends RuntimeException {
    private final TicketStatusErrorCode errorCode;

    public TicketStatusException(TicketStatusErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TicketStatusException(TicketStatusErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TicketStatusErrorCode getErrorCode() {
        return errorCode;
    }
}