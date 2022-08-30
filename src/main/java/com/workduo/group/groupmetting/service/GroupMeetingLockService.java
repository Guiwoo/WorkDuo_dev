package com.workduo.group.groupmetting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMeetingLockService {

    private final RedissonClient redissonClient;

    public void lock(Long groupMeetingId) {
        RLock lock = redissonClient.getLock(getLockKey(groupMeetingId));
        log.error("Trying lock for groupMeetingId : {}", groupMeetingId);

        try {
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);

            if (!isLock) {
                log.error("========Lock acquisition failed========");
                throw new RuntimeException("lock 획득 실패");
            }
        } catch (InterruptedException e) {
            log.error("Redis lock failed");
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void unlock(Long groupMeetingId) {
        log.error("Unlock for groupMeetingId : {}", groupMeetingId);
        redissonClient.getLock(getLockKey(groupMeetingId)).unlock();
    }

    private String getLockKey(Long groupMeetingId) {
        return "LOCK:" + groupMeetingId;
    }
}
