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
    
    // 定义切点：所有需要权限验证的方法
    @Pointcut("@annotation(com.position.airlineorderbackend.annotation.RequireAuth)")
    public void requireAuth() {}
    
    // 定义切点：所有订单相关的操作
    @Pointcut("execution(* com.position.airlineorderbackend.service.OrderService.*(..))")
    public void orderOperations() {}
    
    // 前置通知：权限验证
    @Before("requireAuth()")
    public void checkAuthentication(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.info("【AOP安全】验证 {}.{} 方法的访问权限", className, methodName);
        
        // 这里可以添加具体的权限验证逻辑
        // 例如：检查用户是否登录、是否有相应权限等
        // 如果验证失败，可以抛出异常
    }
    
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