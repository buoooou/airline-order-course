package com.postion.airlineorderbackend.dto;

import org.springframework.http.HttpMethod;
import java.util.Map;

/**
 * 异步HTTP请求数据传输对象
 * 
 * <p>封装异步HTTP请求的完整信息</p>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
public class AsyncHttpRequest {
    
    /** 请求ID */
    private String requestId;
    
    /** 请求URL */
    private String url;
    
    /** 请求方法 */
    private HttpMethod method;
    
    /** 请求头 */
    private Map<String, String> headers;
    
    /** 请求体 */
    private Object body;
    
    /** 回调接口 */
    private AsyncHttpCallback callback;
    
    /**
     * 构造函数
     */
    public AsyncHttpRequest() {}
    
    /**
     * 构造函数
     * 
     * @param url 请求URL
     * @param method 请求方法
     * @param body 请求体
     */
    public AsyncHttpRequest(String url, HttpMethod method, Object body) {
        this.url = url;
        this.method = method;
        this.body = body;
    }
    
    /**
     * 构造函数
     * 
     * @param url 请求URL
     * @param method 请求方法
     * @param headers 请求头
     * @param body 请求体
     * @param callback 回调接口
     */
    public AsyncHttpRequest(String url, HttpMethod method, Map<String, String> headers, 
                           Object body, AsyncHttpCallback callback) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.callback = callback;
    }
    
    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public HttpMethod getMethod() {
        return method;
    }
    
    public void setMethod(HttpMethod method) {
        this.method = method;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    public Object getBody() {
        return body;
    }
    
    public void setBody(Object body) {
        this.body = body;
    }
    
    public AsyncHttpCallback getCallback() {
        return callback;
    }
    
    public void setCallback(AsyncHttpCallback callback) {
        this.callback = callback;
    }
}