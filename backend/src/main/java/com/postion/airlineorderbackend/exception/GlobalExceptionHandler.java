package com.postion.airlineorderbackend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.postion.airlineorderbackend.dto.response.CommonResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle not controled runtime exceptions.
   * 
   * @param e Runtime exception.
   * @return Exception message
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<CommonResponseDto<String>> handleRuntimeException(RuntimeException e) {
    e.printStackTrace();
    return new CommonResponseDto<String>(false, 200, "Unknown error occurred, please contact system administrator.", "")
        .ok();
  }

}
