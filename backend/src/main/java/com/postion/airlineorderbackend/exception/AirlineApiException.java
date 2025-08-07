package com.postion.airlineorderbackend.exception;

public class AirlineApiException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AirlineApiException(String message) {
        super(message);
    }
    public AirlineApiException(String message, Throwable cause) {
        super(message, cause);
    }
}