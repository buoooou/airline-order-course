package com.postion.airlineorderbackend.exception;
import lombok.Data;

import org.springframework.http.HttpStatus;

/**
 * BusinessException
 */
@Data
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;  // Optional: custom error code

    /**
     * BusinessException
     *
     * @param status
     * @param message
     */
    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = null;
    }

    /**
     * BusinessException
     *
     * @param errorCode
     * @param message
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST; // Default 400 error
        this.errorCode = errorCode;
    }

    /**
     * BusinessException
     *
     * @param httpStatus
     * @param errorCode
     * @param message
     */
    public BusinessException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.status = httpStatus;
        this.errorCode = errorCode;
    }

    /**
     * BusinessException
     *
     * @param message
     * @param errorCode
     * @param statusCode
     */
    public BusinessException(String message, String errorCode, int statusCode) {
        super(message);
        this.status = HttpStatus.valueOf(statusCode);
        this.errorCode = errorCode;
    }

    /**
     * orderNotFound
     *
     */
    public static BusinessException orderNotFound() {
        return new BusinessException("Order not found", "ORDER_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    /**
     * notPendingStatus
     *
     */
    public static BusinessException notPaidStatus() {
        return new BusinessException("Ticket can only be issued for PAID orders", "Order cannot be paid as it's not in PENDING status", HttpStatus.BAD_REQUEST.value());
    }

    /**
     * notPendingStatus
     *
     */
    public static BusinessException invalidStatus() {
        return new BusinessException("Invalid status transition from %s to %s for order %d", "Order cannot be paid as it's not in PENDING status", HttpStatus.BAD_REQUEST.value());
    }


    /**
     * insufficientPermissions
     *
     */
    public static BusinessException insufficientPermissions() {
        return new BusinessException("Insufficient permissions", "INSUFFICIENT_PERMISSIONS", HttpStatus.FORBIDDEN.value());
    }

    /**
     * invalidRequest
     *
     */
    public static BusinessException invalidRequest() {
        return new BusinessException("Invalid request", "INVALID_REQUEST", HttpStatus.BAD_REQUEST.value());
    }

    /**
     * unauthorized
     *
     */
    public static BusinessException unauthorized() {
        return new BusinessException("Unauthorized access", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * flightNotAvailable
     *
     */
    public static BusinessException flightNotAvailable() {
        return new BusinessException("Flight not available", "FLIGHT_NOT_AVAILABLE", HttpStatus.NOT_FOUND.value());
    }

    /**
     * seatAlreadyBooked
     *
     */
    public static BusinessException seatAlreadyBooked() {
        return new BusinessException("Seat already booked", "SEAT_ALREADY_BOOKED", HttpStatus.CONFLICT.value());
    }

    /**
     * paymentFailed
     *
     */
    public static BusinessException paymentFailed() {
        return new BusinessException("Payment processing failed", "PAYMENT_FAILED", HttpStatus.PAYMENT_REQUIRED.value());
    }

    /**
     * orderAlreadyCancelled
     *
     */
    public static BusinessException orderAlreadyCancelled() {
        return new BusinessException("Order already cancelled", "ORDER_ALREADY_CANCELLED", HttpStatus.BAD_REQUEST.value());
    }

    /**
     * invalidPassengerInfo
     *
     */
    public static BusinessException invalidPassengerInfo() {
        return new BusinessException("Invalid passenger information", "INVALID_PASSENGER_INFO", HttpStatus.BAD_REQUEST.value());
    }

    /**
     * systemError
     *
     */
    public static BusinessException systemError() {
        return new BusinessException("Internal system error", "INTERNAL_SYSTEM_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}