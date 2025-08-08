package com.postion.airlineorderbackend.dto;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Schema(description = "通用响应包装类")
public class BaseResponse<T> {

	@Schema(description = "HTTP 状态码", example = "200")
    private int status;
	
    @Schema(description = "提示信息", example = "Success")
    private String message;

    @Schema(description = "返回数据")
    private T data;

    // 构造成功响应
    public static <T> BaseResponse<T> success(HttpStatus status, T data) {
        return BaseResponse.<T>builder()
        		.status(status.value())
        		.message(status.getReasonPhrase())
        		.data(data)
        		.build();
    }

 // 自定义状态码和消息
    public static <T> BaseResponse<T> of(HttpStatus status, String message, T data) {
        return  BaseResponse.<T>builder()
        		.status(status.value())
        		.message(message)
        		.data(data)
        		.build();
    }
    
    // 错误响应封装
    public static <T> BaseResponse<T> error(ErrorResponse error) {
        return BaseResponse.<T>builder()
                .status(error.getStatus())
                .message(error.getMessage())
                .data((T) error)
                .build();
    }    
    
    public static <T> BaseResponse<T> failure(HttpStatus status, String message) {
        return BaseResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data(null)
                .build();
    }

}

