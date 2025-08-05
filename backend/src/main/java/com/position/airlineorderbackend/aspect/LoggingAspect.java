package com.position.airlineorderbackend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    
    // 定义切点：所有Service层的方法
    @Pointcut("execution(* com.position.airlineorderbackend.service.*.*(..))")
    public void serviceLayer() {}
    
    // 定义切点：所有Controller层的方法
    @Pointcut("execution(* com.position.airlineorderbackend.controller.*.*(..))")
    public void controllerLayer() {}
    
    // 前置通知：方法执行前记录日志
    @Before("serviceLayer()")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        
        logger.info("【AOP日志】开始执行 {}.{} 方法，参数: {}", 
                   className, methodName, Arrays.toString(args));
    }
    
    // 后置通知：方法执行后记录日志
    @After("serviceLayer()")
    public void logAfterServiceMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.info("【AOP日志】完成执行 {}.{} 方法", className, methodName);
    }
    
    // 环绕通知：记录方法执行时间
    @Around("controllerLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            logger.info("【AOP性能】{}.{} 方法执行完成，耗时: {}ms", 
                       className, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            logger.error("【AOP异常】{}.{} 方法执行异常，耗时: {}ms，异常: {}", 
                        className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }
    
    // 异常通知：记录异常信息
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.error("【AOP异常】{}.{} 方法发生异常: {}", 
                    className, methodName, ex.getMessage());
    }
} 