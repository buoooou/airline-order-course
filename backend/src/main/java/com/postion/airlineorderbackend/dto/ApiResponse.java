package com.postion.airlineorderbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 统一API响应格式
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    /**
     * 响应是否成功
     */
    private boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 错误代码（可选）
     */
    private String error;
    
    /**
     * 分页信息（可选）
     */
    private PaginationInfo pagination;
    
    /**
     * 创建成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("操作成功")
                .data(data)
                .build();
    }
    
    /**
     * 创建成功响应（带消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * 创建成功响应（带分页）
     */
    public static <T> ApiResponse<T> success(String message, T data, PaginationInfo pagination) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .pagination(pagination)
                .build();
    }
    
    /**
     * 创建失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
    
    /**
     * 创建失败响应（带错误代码）
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(errorCode)
                .build();
    }
    
    /**
     * 分页信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int size;
        
        public static PaginationInfo of(org.springframework.data.domain.Page<?> page) {
            return PaginationInfo.builder()
                    .currentPage(page.getNumber())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .size(page.getSize())
                    .build();
        }
    }
}
