package com.position.airlineorderbackend.service;

import com.position.airlineorderbackend.exception.OrderException;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class OrderStateLockService {

    @Autowired
    private LockProvider lockProvider;

    /**
     * 使用ShedLock执行订单状态更新操作
     * @param orderId 订单ID
     * @param operation 要执行的操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public <T> T executeWithLock(Long orderId, Supplier<T> operation) {
        String lockName = "order_state_lock_" + orderId;
        LockConfiguration lockConfig = new LockConfiguration(
            Instant.now(),
            lockName,
            Duration.ofSeconds(30),
            Duration.ofSeconds(5)
        );

        Optional<SimpleLock> lock = lockProvider.lock(lockConfig);
        
        if (lock.isPresent()) {
            try {
                // 断言当前线程持有锁
                LockAssert.assertLocked();
                
                // 执行操作
                return operation.get();
            } finally {
                lock.get().unlock();
            }
        } else {
            throw new OrderException("无法获取订单状态锁，请稍后重试");
        }
    }

    /**
     * 使用ShedLock执行无返回值的订单状态更新操作
     * @param orderId 订单ID
     * @param operation 要执行的操作
     */
    public void executeWithLock(Long orderId, Runnable operation) {
        executeWithLock(orderId, () -> {
            operation.run();
            return null;
        });
    }

    /**
     * 检查订单是否被锁定
     * @param orderId 订单ID
     * @return 是否被锁定
     */
    public boolean isLocked(Long orderId) {
        String lockName = "order_state_lock_" + orderId;
        LockConfiguration lockConfig = new LockConfiguration(
            Instant.now(),
            lockName,
            Duration.ofSeconds(30),
            Duration.ofSeconds(5)
        );

        Optional<SimpleLock> lock = lockProvider.lock(lockConfig);
        if (lock.isPresent()) {
            lock.get().unlock();
            return false; // 能获取到锁说明没有被锁定
        }
        return true; // 无法获取锁说明被锁定
    }
} 