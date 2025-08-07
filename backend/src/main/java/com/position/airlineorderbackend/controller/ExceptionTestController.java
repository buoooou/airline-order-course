package com.position.airlineorderbackend.controller;

import com.position.airlineorderbackend.exception.BusinessException;
import com.position.airlineorderbackend.exception.ResourceNotFoundException;
import com.position.airlineorderbackend.exception.JwtException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@RestController
@RequestMapping("/exception-test")
public class ExceptionTestController {

    /**
     * 测试业务异常
     */
    @GetMapping("/business-exception")
    public String testBusinessException() {
        throw new BusinessException("这是一个业务异常示例");
    }

    /**
     * 测试资源未找到异常
     */
    @GetMapping("/resource-not-found")
    public String testResourceNotFoundException() {
        throw new ResourceNotFoundException("用户", "ID", 999L);
    }

    /**
     * 测试JWT异常
     */
    @GetMapping("/jwt-exception")
    public String testJwtException() {
        throw new JwtException("JWT令牌无效或已过期");
    }

    /**
     * 测试参数验证异常
     */
    @PostMapping("/validation-exception")
    public String testValidationException(@Valid @RequestBody TestRequest request) {
        return "验证成功: " + request.getName();
    }

    /**
     * 测试运行时异常
     */
    @GetMapping("/runtime-exception")
    public String testRuntimeException() {
        throw new RuntimeException("这是一个运行时异常示例");
    }

    /**
     * 测试空指针异常
     */
    @GetMapping("/null-pointer-exception")
    public String testNullPointerException() {
        String str = null;
        return str.length() + ""; // 故意触发空指针异常
    }

    /**
     * 测试除零异常
     */
    @GetMapping("/arithmetic-exception")
    public String testArithmeticException() {
        int result = 10 / 0; // 故意触发除零异常
        return "结果: " + result;
    }

    /**
     * 测试请求参数验证
     */
    @GetMapping("/param-validation")
    public String testParamValidation(@RequestParam @NotBlank @Size(min = 3, max = 10) String name) {
        return "参数验证成功: " + name;
    }

    /**
     * 测试请求体验证
     */
    @Data
    public static class TestRequest {
        @NotBlank(message = "名称不能为空")
        @Size(min = 2, max = 20, message = "名称长度必须在2-20个字符之间")
        private String name;
    }
} 