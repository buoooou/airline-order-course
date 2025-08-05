package com.position.airlineorderbackend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionAspect.class);
    
    // 定义切点：所有带有@Transactional注解的方法
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethods() {}
    
    // 定义切点：所有数据库操作方法
    @Pointcut("execution(* com.position.airlineorderbackend.repo.*.*(..))")
    public void databaseOperations() {}
    
    // 前置通知：事务开始
    @Before("transactionalMethods()")
    public void beforeTransaction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.info("【AOP事务】开始事务: {}.{}", className, methodName);
    }
    
    // 异常通知：事务回滚
    @AfterThrowing(pointcut = "transactionalMethods()", throwing = "ex")
    public void afterThrowingTransaction(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.error("【AOP事务】事务回滚: {}.{}，异常: {}", 
                    className, methodName, ex.getMessage());
    }
    
    // 前置通知：数据库操作监控
    @Before("databaseOperations()")
    public void beforeDatabaseOperation(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        
        logger.info("【AOP数据库】执行数据库操作: {}.{}，参数: {}", 
                   className, methodName, args.length > 0 ? args[0] : "无参数");
    }
} 