package com.postion.airlineorderbackend.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis(); // 添加时间戳

    /**
     * Request successful
     *
     * @param data
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Request successful");
    }

    /**
     * Request successful
     *
     * @param data
     * @param message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * Request failure
     *
     * @param code
     * @param message
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return error(code, message, null);
    }

    /**
     * Request failure
     *
     * @param code
     * @param message
     * @param data
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * Request unauthorized
     *
     * @param message
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(HttpStatus.UNAUTHORIZED.value(), message);
    }

    /**
     * Request notFound
     *
     * @param message
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(HttpStatus.NOT_FOUND.value(), message);
    }
}