package com.position.airlineorderbackend.aspect;

import com.position.airlineorderbackend.service.OrderService;
import com.position.airlineorderbackend.annotation.RequireAuth;
import org.springframework.web.bind.annotation.*;

/**
 * AOP演示控制器
 * 用于展示AOP的各种功能和效果
 */
@RestController
@RequestMapping("/aop-demo")
public class AopDemoController {
    
    private final OrderService orderService;
    
    public AopDemoController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * 演示日志切面
     * 这个方法会被LoggingAspect拦截，记录执行时间和日志
     */
    @GetMapping("/logging-demo")
    public String loggingDemo() {
        // 模拟一些处理时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "日志切面演示完成";
    }
    
    /**
     * 演示安全切面
     * 这个方法会被SecurityAspect拦截，进行权限验证
     */
    @GetMapping("/security-demo")
    @RequireAuth(value = "ADMIN", loginRequired = true)
    public String securityDemo() {
        return "安全切面演示完成";
    }
    
    /**
     * 演示事务切面
     * 这个方法会触发数据库操作，被TransactionAspect监控
     */
    @GetMapping("/transaction-demo/{id}")
    public String transactionDemo(@PathVariable Long id) {
        // 调用Service方法，会触发事务切面
        orderService.getOrderById(id);
        return "事务切面演示完成";
    }
    
    /**
     * 演示异常处理
     * 这个方法会抛出异常，被AOP捕获并记录
     */
    @GetMapping("/exception-demo")
    public String exceptionDemo() {
        // 故意抛出异常来演示异常通知
        throw new RuntimeException("这是一个演示异常");
    }
} 