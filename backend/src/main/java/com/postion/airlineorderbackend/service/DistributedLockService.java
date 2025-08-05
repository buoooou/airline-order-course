package com.postion.airlineorderbackend.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final StringRedisTemplate redisTemplate;
    private static final String FLIGHT_LOCK_PREFIX = "flight:lock:";

    public boolean acquireLock(String lockKey, String requestId, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.MILLISECONDS);
    }

    public boolean releaseLock(String lockKey, String requestId) {
        String script =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "   return redis.call('del', KEYS[1]) " +
            "else " +
            "   return 0 " +
            "end";
        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class),
            Collections.singletonList(lockKey),
            requestId
        );
        return result != null && result == 1;
    }

    public boolean renewLock(String lockKey, String requestId, long expireTime) {
        String script =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "   return redis.call('pexpire', KEYS[1], ARGV[2]) " +
            "else " +
            "   return 0 " +
            "end";
        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(script, Long.class),
            Collections.singletonList(lockKey),
            requestId,
            String.valueOf(expireTime)
        );
        return result != null && result == 1;
    }

    public boolean tryFlightLock(String flightNo, long expireTime) {
        String lockKey = FLIGHT_LOCK_PREFIX + flightNo;
        String requestId = UUID.randomUUID().toString();
        return acquireLock(lockKey, requestId, expireTime);
    }
}