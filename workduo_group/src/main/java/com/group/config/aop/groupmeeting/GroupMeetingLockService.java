package com.group.config.aop.groupmeeting;

import com.core.error.group.exception.GroupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.core.error.group.type.GroupErrorCode.GROUP_MEETING_LOCK_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMeetingLockService {

    private final RedissonClient redissonClient;

    public void lock(Long groupMeetingId, long leaseTime) throws InterruptedException {
        RLock lock = redissonClient.getLock(getLockKey(groupMeetingId));
        log.info("Trying lock for groupMeetingId : {}", groupMeetingId);

        try {
            boolean isLock = lock.tryLock(3, leaseTime, TimeUnit.SECONDS);

            if (!isLock) {
                throw new GroupException(GROUP_MEETING_LOCK_FAIL);
            }
        } catch (GroupException e) {
            throw e;
        }
    }

    public void unlock(Long groupMeetingId) {
        log.info("Unlock for groupMeetingId : {}", groupMeetingId);
        redissonClient.getLock(getLockKey(groupMeetingId)).unlock();
    }

    private String getLockKey(Long groupMeetingId) {
        return "GROUP_MEETING_LOCK:" + groupMeetingId;
    }
}
