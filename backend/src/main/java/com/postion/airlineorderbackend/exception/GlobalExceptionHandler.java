package com.postion.airlineorderbackend.exception;

import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.enums.TicketStatusErrorCode;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TicketStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleTicketStatusException(TicketStatusException ex) {
        TicketStatusErrorCode errorCode = ex.getErrorCode();
        log.error("TicketStatusException occurred: code={}, message={}", errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getCode() / 100)
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Internal Server Error: " + ex.getMessage()));
    }
}