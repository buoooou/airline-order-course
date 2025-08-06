package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.AsyncHttpRequest;
import com.postion.airlineorderbackend.dto.AsyncHttpResponse;
import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.service.AsyncHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 异步HTTP请求控制器
 * 
 * <p>
 * 提供异步HTTP请求的REST API接口
 * </p>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
@RestController
@RequestMapping("/api/async-http")
public class AsyncHttpController {

    @Autowired
    private AsyncHttpService asyncHttpService;

    /**
     * 提交固定API的异步HTTP请求
     * 
     * @return 响应结果
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<String>> submitRequest() {
        try {
            // 固定调用JSONPlaceholder的测试API
            AsyncHttpRequest asyncRequest = new AsyncHttpRequest();
            asyncRequest.setUrl("https://jsonplaceholder.typicode.com/posts/1");
            asyncRequest.setMethod(HttpMethod.GET);
            asyncRequest.setHeaders(null);
            asyncRequest.setBody(null);

            String requestId = asyncHttpService.submitRequest(asyncRequest);

            return ResponseEntity.ok(ApiResponse.success("Fixed API request submitted successfully", requestId));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to submit fixed API request: " + e.getMessage()));
        }
    }

    /**
     * 同步等待异步HTTP请求结果
     * 
     * @param requestId 请求ID
     * @param timeout   超时时间（秒）
     * @return 响应结果
     */
    @GetMapping("/result/{requestId}")
    public ResponseEntity<ApiResponse<AsyncHttpResponse>> getResult(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "30") int timeout) {

        try {
            AsyncHttpResponse response = asyncHttpService.waitForResult(requestId, timeout);

            if (response == null) {
                return ResponseEntity
                        .ok(ApiResponse.error("Request not found or timeout"));
            }

            return ResponseEntity.ok(ApiResponse.success("Request completed", response));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get result: " + e.getMessage()));
        }
    }

    /**
     * 获取队列状态
     * 
     * @return 队列状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        try {
            Map<String, Object> status = asyncHttpService.getQueueStatus();
            return ResponseEntity
                    .ok(ApiResponse.success("Status retrieved successfully", status));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get status: " + e.getMessage()));
        }
    }

    /**
     * 清理已完成的请求缓存
     * 
     * @return 清理结果
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<ApiResponse<String>> cleanup() {
        try {
            asyncHttpService.cleanup();
            return ResponseEntity.ok(ApiResponse.success("Cleanup completed", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to cleanup: " + e.getMessage()));
        }
    }
}