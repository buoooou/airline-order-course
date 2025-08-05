package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.util.EmailNotifier;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlightStatusMonitorService {

    private final DistributedLockService lockService;
    private final EmailNotifier emailNotifier;

    @Scheduled(fixedRate = 5000)
    public void monitorFlightStatus() {
        String lockKey = "flight:status:monitor";
        String requestId = "monitor-job";
        long expireTime = 10000; // 10 seconds

        if (lockService.acquireLock(lockKey, requestId, expireTime)) {
            try {
                // Simulate API call to check flight status
                boolean isStatusNormal = checkFlightStatus();
                if (!isStatusNormal) {
                    emailNotifier.sendAlert("Flight status abnormal!");
                }
            } finally {
                lockService.releaseLock(lockKey, requestId);
            }
        }
    }

    private boolean checkFlightStatus() {
        // Placeholder for actual API call
        return true;
    }
}