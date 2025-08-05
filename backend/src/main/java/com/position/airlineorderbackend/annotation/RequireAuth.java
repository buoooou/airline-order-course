package com.position.airlineorderbackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：标记需要权限验证的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
    /**
     * 需要的权限级别
     */
    String value() default "USER";
    
    /**
     * 是否需要登录
     */
    boolean loginRequired() default true;
} 