package com.postion.airlineorderbackend.dto;

import org.springframework.http.HttpHeaders;

/**
 * 异步HTTP响应数据传输对象
 * 
 * <p>封装异步HTTP请求的响应结果</p>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
public class AsyncHttpResponse {
    
    /** 请求ID */
    private String requestId;
    
    /** HTTP状态码 */
    private int statusCode;
    
    /** 响应体 */
    private String body;
    
    /** 响应头 */
    private HttpHeaders headers;
    
    /** 处理时间（毫秒） */
    private long processingTime;
    
    /** 是否成功 */
    private boolean success;
    
    /** 错误消息 */
    private String errorMessage;
    
    /**
     * 构造函数
     */
    public AsyncHttpResponse() {}
    
    /**
     * 构造函数
     * 
     * @param requestId 请求ID
     * @param statusCode HTTP状态码
     * @param body 响应体
     * @param headers 响应头
     * @param processingTime 处理时间
     * @param success 是否成功
     * @param errorMessage 错误消息
     */
    public AsyncHttpResponse(String requestId, int statusCode, String body, 
                           HttpHeaders headers, long processingTime, boolean success, 
                           String errorMessage) {
        this.requestId = requestId;
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
        this.processingTime = processingTime;
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public HttpHeaders getHeaders() {
        return headers;
    }
    
    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }
    
    public long getProcessingTime() {
        return processingTime;
    }
    
    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return "AsyncHttpResponse{" +
                "requestId='" + requestId + '\'' +
                ", statusCode=" + statusCode +
                ", body='" + body + '\'' +
                ", processingTime=" + processingTime +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}