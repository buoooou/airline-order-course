package com.postion.airlineorderbackend.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.postion.airlineorderbackend.constants.Constants;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "统一API响应")
public class ApiResponseDTO<T> {
    @Schema(description = "HTTP Status")
    private int code;
    @Schema(description = "业务消息")
    private String message;
    @Schema(description = "响应数据")
    private T data;
    private boolean success;

    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(Constants.API_RES_SUCCESS)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> success(int code, T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .code(code)
                .message(Constants.API_RES_SUCCESS)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> success(int code, String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(int code) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .code(code)
                .message(Constants.API_RES_ERROR)
                .data(null)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(int code, String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(int code, String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}
