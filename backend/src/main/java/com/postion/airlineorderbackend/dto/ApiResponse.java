package com.postion.airlineorderbackend.dto;

import lombok.Data;
import com.postion.airlineorderbackend.constants.BusinessConstants;

@Data
public class ApiResponse<T> implements BusinessConstants {

    private int code;
    private String message;
    private T data;

    // 预设成功响应
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(SUCCESS);
        response.setData(data);
        return response;
    }

    // 预设失败响应
    public static <T> ApiResponse<T> error(int code, String message) {
    	ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}
