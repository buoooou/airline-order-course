package com.postion.airlineorderbackend.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.postion.airlineorderbackend.dto.OrderResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Runtime exceptions.
   * 
   * @param e Runtime exception.
   * @return Exception message
   */
  @ExceptionHandler(RuntimeException.class)
  public OrderResponseDTO handleRuntimeException(RuntimeException e) {
    e.printStackTrace();
    return new OrderResponseDTO();
  }

}