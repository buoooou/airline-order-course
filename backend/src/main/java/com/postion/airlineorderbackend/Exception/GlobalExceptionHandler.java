package com.postion.airlineorderbackend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.postion.airlineorderbackend.constants.Constants;
import com.postion.airlineorderbackend.dto.ApiResponseDTO;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AirlineBusinessException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(AirlineBusinessException e) {
        log.error("{} HttpStatus:{}, message:{}", AirlineBusinessException.class.getName(), e.getCode(), e.getMessage());
        System.out.println("AirlineBusinessExceptionHandler# message:" + e.getMessage());
        ApiResponseDTO<?> response = ApiResponseDTO.error(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getCode()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(IllegalStateException e) {
        log.error("{}: {}", IllegalStateException.class.getName(), e.getMessage());
        System.out.println("IllegalStateExceptionHandler# message:" + e.getMessage());
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(AuthenticationException e) {
        log.error("{}: {}", AuthenticationException.class.getName(), e.getMessage());
        System.out.println("AuthenticationExceptionHandler# message:" + e.getMessage());
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleGlobalException(Exception e) {
        log.error("{}: {}", AirlineBusinessException.class.getName(), e.getMessage());
        System.out.println("ExceptionHandler# message:" + e.getMessage());
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.GLOBAL_ERROR_MSG);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
