package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.constants.BizConsts;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private final int code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, BizConsts.SUCCESS, data);
    }

    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}