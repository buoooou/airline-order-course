package com.postion.airlineorderbackend.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BussinessException extends RuntimeException {
	private final HttpStatus status;
	public BussinessException(HttpStatus status,String message) {
		super(message);
		this.status = status;
	}
}
