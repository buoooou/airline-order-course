package com.postion.airlineorderbackend.exception;

public class InvalidCredentialsException extends RuntimeException {
	public InvalidCredentialsException(String message) {
		super(message);
	}
}
