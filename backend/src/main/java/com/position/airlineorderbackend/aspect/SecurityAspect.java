package com.position.airlineorderbackend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);
    
    // 定义切点：所有订单相关的操作
    @Pointcut("execution(* com.position.airlineorderbackend.service.OrderService.*(..))")
    public void orderOperations() {}
    
    // 前置通知：订单操作权限验证
    @Before("orderOperations()")
    public void checkOrderPermission(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        if (args.length > 0 && args[0] instanceof Long) {
            Long orderId = (Long) args[0];
            logger.info("【AOP安全】验证订单 {} 的操作权限，方法: {}", orderId, methodName);
            
            // 这里可以添加订单权限验证逻辑
            // 例如：检查用户是否有权限操作该订单
        }
    }
} 