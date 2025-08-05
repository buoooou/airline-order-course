package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.common.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一API响应结果类
 *
 * 该类用于标准化所有API接口的响应格式，确保前后端数据交互的一致性。
 * 支持泛型，可以包装任意类型的响应数据。
 *
 * @param <T> 响应数据的类型参数
 *
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 响应结果状态
     * SUCCESS - 成功
     * ERROR - 错误
     */
    private String result;

    /**
     * 响应消息
     * 通常用于描述响应结果或错误信息
     */
    private String message;

    /**
     * 响应数据
     * 泛型字段，可以存储任意类型的响应数据
     */
    private T data;

    /**
     * 创建成功响应（默认消息）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应对象
     *
     * @example
     *          ApiResponse<User> response = ApiResponse.success(user);
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", Constants.HttpStatusMessage.SUCCESS, data);
    }

    /**
     * 创建成功响应（自定义消息）
     *
     * @param message 成功消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 成功响应对象
     *
     * @example
     *          ApiResponse<User> response = ApiResponse.success("用户创建成功", user);
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    /**
     * 创建错误响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误响应对象
     *
     * @example
     *          ApiResponse<Object> response = ApiResponse.error("用户不存在");
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", message, null);
    }

    /**
     * 创建错误响应（带数据）
     *
     * @param message 错误消息
     * @param data    错误数据
     * @param <T>     数据类型
     * @return 错误响应对象
     *
     * @example
     *          ApiResponse<Map> response = ApiResponse.error("参数错误", errorMap);
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>("ERROR", message, data);
    }
}