package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.AsyncHttpRequest;
import com.postion.airlineorderbackend.dto.AsyncHttpResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 异步HTTP请求服务
 * 
 * <p>
 * 使用@Async + CompletableFuture实现简化的异步HTTP请求处理
 * </p>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
@Service
public class AsyncHttpService {

    private static final Logger logger = Logger.getLogger(AsyncHttpService.class.getName());

    private final RestTemplate restTemplate;
    private final Executor asyncExecutor;
    private final ConcurrentHashMap<String, CompletableFuture<AsyncHttpResponse>> futureCache;
    private final ConcurrentHashMap<String, AsyncHttpResponse> responseCache;

    public AsyncHttpService(RestTemplate restTemplate, Executor asyncExecutor) {
        this.restTemplate = restTemplate;
        this.asyncExecutor = asyncExecutor;
        this.futureCache = new ConcurrentHashMap<>();
        this.responseCache = new ConcurrentHashMap<>();
    }

    /**
     * 提交异步HTTP请求
     * 
     * @param request 请求对象
     * @return 请求ID
     */
    public String submitRequest(AsyncHttpRequest request) {
        String requestId = generateRequestId();
        request.setRequestId(requestId);

        logger.log(Level.INFO, "异步请求开始: {0}, 线程ID: {1}, 线程名称: {2}",
                new Object[] { requestId, Thread.currentThread().getId(), Thread.currentThread().getName() });

        // 创建异步任务
        CompletableFuture<AsyncHttpResponse> future = executeAsyncRequest(request);
        futureCache.put(requestId, future);

        // 处理完成后缓存结果并执行回调
        future.whenComplete((response, throwable) -> {
            long completeThreadId = Thread.currentThread().getId();
            if (throwable != null) {
                AsyncHttpResponse errorResponse = new AsyncHttpResponse(
                        requestId, 500, null, null, 0, false, throwable.getMessage());
                responseCache.put(requestId, errorResponse);
                if (request.getCallback() != null) {
                    request.getCallback().onFailure(errorResponse);
                }
                logger.log(Level.INFO, "whenComplete 异步请求处理失败: {0}, 线程ID: {1}, 线程名称: {2}",
                        new Object[] { requestId, completeThreadId, Thread.currentThread().getName() });
            } else {
                responseCache.put(requestId, response);
                if (request.getCallback() != null) {
                    request.getCallback().onSuccess(response);
                }
                logger.log(Level.INFO, "whenComplete 异步请求处理完成: {0}, 线程ID: {1}, 线程名称: {2}",
                        new Object[] { requestId, completeThreadId, Thread.currentThread().getName() });
            }
            futureCache.remove(requestId);
        });

        logger.log(Level.INFO, "异步请求已提交: {0}, 线程ID: {1}, 线程名称: {2}",
                new Object[] { requestId, Thread.currentThread().getId(), Thread.currentThread().getName() });
        return requestId;
    }

    /**
     * 执行异步HTTP请求
     * 
     * @param request 请求对象
     * @return CompletableFuture包装的响应结果
     */
    public CompletableFuture<AsyncHttpResponse> executeAsyncRequest(AsyncHttpRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            long threadId = Thread.currentThread().getId();

            try {
                logger.log(Level.INFO, "开始执行请求: {0}, 线程ID: {1}, 线程名称: {2}",
                        new Object[] { request.getRequestId(), threadId, Thread.currentThread().getName() });

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                if (request.getHeaders() != null) {
                    request.getHeaders().forEach(headers::add);
                }

                HttpEntity<Object> entity = new HttpEntity<>(request.getBody(), headers);

                logger.log(Level.INFO, " restTemplate.exchange 开始执行请求: {0}, 线程ID: {1}",
                        new Object[] { request.getRequestId(), threadId });

                ResponseEntity<String> response = restTemplate.exchange(
                        request.getUrl(),
                        request.getMethod(),
                        entity,
                        String.class);
                logger.log(Level.INFO, "new AsyncHttpResponse 开始执行请求: {0}, 线程ID: {1}",
                        new Object[] { request.getRequestId(), threadId });

                AsyncHttpResponse asyncResponse = new AsyncHttpResponse(
                        request.getRequestId(),
                        response.getStatusCode().value(),
                        response.getBody(),
                        response.getHeaders(),
                        System.currentTimeMillis() - startTime,
                        true,
                        null);

                logger.log(Level.INFO, "请求执行完成: {0}, 耗时: {1}ms, 线程ID: {2}, 线程名称: {3}",
                        new Object[] { request.getRequestId(), asyncResponse.getProcessingTime(), threadId,
                                Thread.currentThread().getName() });

                return asyncResponse;

            } catch (Exception e) {
                logger.log(Level.SEVERE, "请求执行失败: " + request.getRequestId() + ", 线程ID: " + threadId + ", 线程名称: "
                        + Thread.currentThread().getName(), e);

                AsyncHttpResponse errorResponse = new AsyncHttpResponse(
                        request.getRequestId(),
                        500,
                        null,
                        null,
                        System.currentTimeMillis() - startTime,
                        false,
                        e.getMessage());

                return errorResponse;
            }
        }, asyncExecutor);
    }

    /**
     * 同步等待获取请求结果
     * 
     * @param requestId 请求ID
     * @param timeout   超时时间（秒）
     * @return 响应结果
     */
    public AsyncHttpResponse waitForResult(String requestId, int timeout) {
        CompletableFuture<AsyncHttpResponse> future = futureCache.get(requestId);
        if (future == null) {
            return responseCache.get(requestId);
        }

        try {
            return future.get(timeout, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.log(Level.WARNING, "等待结果超时或异常: {0}", requestId);
            AsyncHttpResponse timeoutResponse = new AsyncHttpResponse(
                    requestId, 408, null, null, 0, false, "Request timeout or interrupted");
            responseCache.put(requestId, timeoutResponse);
            return timeoutResponse;
        }
    }

    /**
     * 获取服务状态
     * 
     * @return 状态信息
     */
    public Map<String, Object> getQueueStatus() {
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("activeRequests", futureCache.size());
        status.put("cachedResponses", responseCache.size());
        return status;
    }

    /**
     * 清理缓存
     */
    public void cleanup() {
        responseCache.clear();
        logger.info("响应缓存已清理");
    }

    /**
     * 获取请求结果
     * 
     * @param requestId 请求ID
     * @return 响应结果
     */
    public AsyncHttpResponse getRequestResult(String requestId) {
        return responseCache.get(requestId);
    }

    /**
     * 生成请求ID
     * 
     * @return 唯一请求ID
     */
    private String generateRequestId() {
        return "REQ_" + UUID.randomUUID().toString().substring(0, 8);
    }
}