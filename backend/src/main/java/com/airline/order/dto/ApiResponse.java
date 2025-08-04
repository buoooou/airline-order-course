package com.airline.order.dto;

/**
 * 通用API响应封装类
 * @param <T> 响应数据类型
 */
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    /**
     * 默认构造函数
     */
    public ApiResponse() {
    }

    /**
     * 构造函数
     * @param success 是否成功
     * @param message 消息
     * @param data 数据
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应
     * @param data 数据
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data);
    }

    /**
     * 创建成功响应
     * @param message 消息
     * @param data 数据
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 创建失败响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * 创建失败响应
     * @param message 错误消息
     * @param data 数据
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }

    /**
     * 获取是否成功
     * @return 是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置是否成功
     * @param success 是否成功
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取消息
     * @return 消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置消息
     * @param message 消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取数据
     * @return 数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置数据
     * @param data 数据
     */
    public void setData(T data) {
        this.data = data;
    }
}