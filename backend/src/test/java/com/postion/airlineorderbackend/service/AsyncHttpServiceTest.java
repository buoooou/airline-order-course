package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.AsyncHttpRequest;
import com.postion.airlineorderbackend.dto.AsyncHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * AsyncHttpService测试类
 * 
 * <p>测试异步HTTP请求服务的各种场景</p>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
@ExtendWith(MockitoExtension.class)
class AsyncHttpServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private Executor asyncExecutor;

    private AsyncHttpService asyncHttpService;

    @BeforeEach
    void setUp() {
        asyncExecutor = Executors.newFixedThreadPool(2);
        asyncHttpService = new AsyncHttpService(restTemplate, asyncExecutor);
    }

    /**
     * 测试场景1：请求超时
     * 
     * <p>模拟第三方API响应超时的情况</p>
     */
    @Test
    void testRequestTimeout() {
        // 创建测试请求
        AsyncHttpRequest request = new AsyncHttpRequest();
        request.setUrl("https://httpbin.org/delay/5");
        request.setMethod(HttpMethod.GET);

        // 模拟超时异常
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenAnswer(invocation -> {
                    Thread.sleep(3000); // 模拟长时间响应
                    return ResponseEntity.ok("Delayed response");
                });

        // 提交请求并等待结果
        String requestId = asyncHttpService.submitRequest(request);
        
        // 使用较短的超时时间测试超时情况
        AsyncHttpResponse response = asyncHttpService.waitForResult(requestId, 1);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(408, response.getStatusCode());
        assertFalse(response.isSuccess());
        assertEquals("Request timeout or interrupted", response.getErrorMessage());
    }

    /**
     * 测试场景2：网络连接失败
     * 
     * <p>模拟网络连接不可达的情况</p>
     */
    @Test
    void testNetworkConnectionFailure() {
        // 创建测试请求
        AsyncHttpRequest request = new AsyncHttpRequest();
        request.setUrl("https://nonexistent-api.example.com/data");
        request.setMethod(HttpMethod.GET);

        // 模拟网络连接失败
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        // 提交请求并等待结果
        String requestId = asyncHttpService.submitRequest(request);
        AsyncHttpResponse response = asyncHttpService.waitForResult(requestId, 3);

        // 验证结果
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
        assertFalse(response.isSuccess());
        assertTrue(response.getErrorMessage().contains("Connection refused"));
    }

    /**
     * 测试场景3：资源不存在（404错误）
     * 
     * <p>模拟第三方API返回404资源不存在的情况</p>
     */
    @Test
    void testResourceNotFound() {
        // 创建测试请求
        AsyncHttpRequest request = new AsyncHttpRequest();
        request.setUrl("https://jsonplaceholder.typicode.com/posts/999999");
        request.setMethod(HttpMethod.GET);

        // 模拟404错误
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(ResponseEntity.notFound().build());

        // 提交请求并等待结果
        String requestId = asyncHttpService.submitRequest(request);
        AsyncHttpResponse response = asyncHttpService.waitForResult(requestId, 5);

        // 验证结果
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
        assertTrue(response.isSuccess()); // 注意：在AsyncHttpService中，只要请求完成就算success=true
    }

    /**
     * 测试异步执行是否正常
     * 
     * <p>验证请求是否在异步线程中执行</p>
     */
    @Test
    void testAsyncExecution() throws Exception {
        // 创建测试请求
        AsyncHttpRequest request = new AsyncHttpRequest();
        request.setUrl("https://jsonplaceholder.typicode.com/posts/1");
        request.setMethod(HttpMethod.GET);

        // 模拟正常响应
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1,\"title\":\"Test\"}"));

        // 提交异步请求
        String requestId = asyncHttpService.submitRequest(request);
        assertNotNull(requestId);

        // 等待结果
        AsyncHttpResponse response = asyncHttpService.waitForResult(requestId, 3);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
        assertEquals("{\"id\":1,\"title\":\"Test\"}", response.getBody());
    }

    /**
     * 测试服务状态查询
     */
    @Test
    void testQueueStatus() {
        // 初始状态应该为空
        var status = asyncHttpService.getQueueStatus();
        assertEquals(0, status.get("activeRequests"));
        assertEquals(0, status.get("cachedResponses"));
        
        // 验证状态查询功能正常
        assertNotNull(status);
        assertTrue(status.containsKey("activeRequests"));
        assertTrue(status.containsKey("cachedResponses"));
    }

    /**
     * 测试清理缓存
     */
    @Test
    void testCleanup() {
        // 创建测试请求
        AsyncHttpRequest request = new AsyncHttpRequest();
        request.setUrl("https://jsonplaceholder.typicode.com/posts/1");
        request.setMethod(HttpMethod.GET);

        // 模拟正常响应
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("Test response"));

        // 提交请求并等待完成
        String requestId = asyncHttpService.submitRequest(request);
        asyncHttpService.waitForResult(requestId, 3);

        // 清理缓存
        asyncHttpService.cleanup();
        
        // 验证缓存已清空
        var status = asyncHttpService.getQueueStatus();
        assertEquals(0, status.get("cachedResponses"));
    }
}