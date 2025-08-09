package com.postion.airlineorderbackend.exception;

public class InvalidOrderStatusException extends RuntimeException {

  public InvalidOrderStatusException() {
    super("Invalid order status.");
  }

  public InvalidOrderStatusException(String message) {
    super(message);
  }

}
