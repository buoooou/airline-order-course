package com.postion.airlineorderbackend.common;

/**
 * 项目常量定义类
 * 
 * <p>包含项目中常用的字符串常量定义，避免硬编码，提高代码可维护性</p>
 * 
 * <p>功能包括：</p>
 * <ul>
 *     <li>API路径常量定义</li>
 *     <li>HTTP状态消息常量</li>
 *     <li>认证相关常量定义</li>
 *     <li>通用错误和成功消息定义</li>
 * </ul>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
public final class Constants {

    /**
     * 私有构造函数防止实例化
     */
    private Constants() {
        throw new AssertionError("Constants类不可实例化");
    }

    /**
     * API路径常量
     */
    public static final class ApiPath {
        /** 认证相关API前缀 */
        public static final String AUTH_PREFIX = "/api/auth";
        /** Swagger文档路径 */
        public static final String SWAGGER_UI = "/swagger-ui/**";
        /** Swagger API文档路径 */
        public static final String SWAGGER_API_DOCS = "/v3/api-docs/**";
    }

    /**
     * HTTP状态消息常量
     */
    public static final class HttpStatusMessage {
        /** 成功消息 */
        public static final String SUCCESS = "操作成功";
        /** 未找到资源消息 */
        public static final String NOT_FOUND = "资源未找到";
        /** 服务器内部错误消息 */
        public static final String INTERNAL_ERROR = "服务器内部错误";
    }

    /**
     * 认证相关常量
     */
    public static final class Auth {
        /** JWT令牌前缀 */
        public static final String TOKEN_PREFIX = "Bearer ";
        /** JWT令牌请求头名称 */
        public static final String AUTHORIZATION_HEADER = "Authorization";
        /** 默认角色 */
        public static final String DEFAULT_ROLE = "USER";
    }

    /**
     * 通用错误消息常量
     */
    public static final class ErrorMessage {
        /** 用户名已存在 */
        public static final String USERNAME_EXISTS = "用户名已存在";
        /** 用户名或密码错误 */
        public static final String INVALID_CREDENTIALS = "用户名或密码错误";
        /** 参数校验失败 */
        public static final String VALIDATION_FAILED = "参数校验失败";
    }

    /**
     * 通用成功消息常量
     */
    public static final class SuccessMessage {
        /** 登录成功 */
        public static final String LOGIN_SUCCESS = "登录成功";
        /** 注册成功 */
        public static final String REGISTER_SUCCESS = "注册成功";
    }

    /**
     * 时间相关常量
     */
    public static final class Time {
        /** 默认JWT过期时间（毫秒） */
        public static final long JWT_EXPIRATION = 86400000L; // 24小时
        /** 默认分页大小 */
        public static final int DEFAULT_PAGE_SIZE = 20;
    }

    /**
     * 正则表达式常量
     */
    public static final class Regex {
        /** 邮箱验证正则 */
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        /** 密码强度验证正则（至少8位，包含字母和数字） */
        public static final String PASSWORD_STRONG = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";
        /** 用户名验证正则（字母、数字、下划线，3-20位） */
        public static final String USERNAME = "^[a-zA-Z0-9_]{3,20}$";
    }
}