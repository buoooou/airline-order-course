package com.postion.airlineorderbackend.dto;

/**
 * 异步HTTP回调接口
 * 
 * <p>定义异步HTTP请求完成后的回调方法</p>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
public interface AsyncHttpCallback {
    
    /**
     * 请求成功时的回调方法
     * 
     * @param response 响应对象
     */
    void onSuccess(AsyncHttpResponse response);
    
    /**
     * 请求失败时的回调方法
     * 
     * @param response 响应对象
     */
    void onFailure(AsyncHttpResponse response);
}