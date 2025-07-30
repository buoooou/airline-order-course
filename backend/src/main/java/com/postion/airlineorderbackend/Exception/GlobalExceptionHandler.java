package com.postion.airlineorderbackend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.postion.airlineorderbackend.dto.ApiResponseDTO;
import com.postion.airlineorderbackend.constants.Constants;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AirlineBusinessException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(AirlineBusinessException e) {
        ApiResponseDTO<?> response = ApiResponseDTO.error(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getCode()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(IllegalStateException e) {
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleGlobalException(Exception e) {
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.GLOBAL_ERROR_MSG);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
