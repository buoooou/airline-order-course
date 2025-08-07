package com.postion.airlineorderbackend.exception;


public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException() {
    super("Data not found.");
  }

  public UserNotFoundException(String message) {
    super(message);
  }

}