package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.AsyncHttpCallback;
import com.postion.airlineorderbackend.dto.AsyncHttpRequest;
import com.postion.airlineorderbackend.dto.AsyncHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 异步HTTP请求示例服务
 * 
 * <p>提供异步HTTP请求的使用示例</p>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
@Service
public class AsyncHttpExampleService {
    
    @Autowired
    private AsyncHttpService asyncHttpService;
    
    /**
     * 示例：发送异步GET请求
     * 
     * @param url 请求URL
     * @return 请求ID
     */
    public String sendAsyncGetRequest(String url) {
        AsyncHttpRequest request = new AsyncHttpRequest();
        request.setUrl(url);
        request.setMethod(HttpMethod.GET);
        
        request.setCallback(new AsyncHttpCallback() {
            @Override
            public void onSuccess(AsyncHttpResponse response) {
                System.out.println("GET请求成功: " + response.getStatusCode());
                System.out.println("响应内容: " + response.getBody());
            }
            
            @Override
            public void onFailure(AsyncHttpResponse response) {
                System.err.println("GET请求失败: " + response.getErrorMessage());
            }
        });
        
        return asyncHttpService.submitRequest(request);
    }
    
    /**
     * 示例：发送异步POST请求
     * 
     * @param url 请求URL
     * @param data POST数据
     * @return 请求ID
     */
    public String sendAsyncPostRequest(String url, Map<String, Object> data) {
        AsyncHttpRequest request = new AsyncHttpRequest();
        request.setUrl(url);
        request.setMethod(HttpMethod.POST);
        request.setBody(data);
        
        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        request.setHeaders(headers);
        
        request.setCallback(new AsyncHttpCallback() {
            @Override
            public void onSuccess(AsyncHttpResponse response) {
                System.out.println("POST请求成功: " + response.getStatusCode());
                System.out.println("响应内容: " + response.getBody());
            }
            
            @Override
            public void onFailure(AsyncHttpResponse response) {
                System.err.println("POST请求失败: " + response.getErrorMessage());
            }
        });
        
        return asyncHttpService.submitRequest(request);
    }
    
    /**
     * 示例：批量发送异步请求
     * 
     * @param urls URL列表
     */
    public void sendBatchAsyncRequests(String[] urls) {
        for (String url : urls) {
            sendAsyncGetRequest(url);
        }
    }
    
    /**
     * 示例：使用同步等待获取结果
     * 
     * @param url 请求URL
     * @return 响应结果
     */
    public AsyncHttpResponse sendSyncWithAsyncBackend(String url) {
        AsyncHttpRequest request = new AsyncHttpRequest();
        request.setUrl(url);
        request.setMethod(HttpMethod.GET);
        
        String requestId = asyncHttpService.submitRequest(request);
        
        // 等待最多10秒获取结果
        return asyncHttpService.waitForResult(requestId, 10);
    }
}