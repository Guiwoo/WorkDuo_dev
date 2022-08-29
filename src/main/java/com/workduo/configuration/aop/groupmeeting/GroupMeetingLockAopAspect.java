package com.workduo.configuration.aop.groupmeeting;

import com.workduo.group.groupmetting.service.GroupMeetingLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class GroupMeetingLockAopAspect {

    private final GroupMeetingLockService groupMeetingLockService;

    @Around("@annotation(com.workduo.configuration.aop.groupmeeting.GroupMeetingLock) && args(request)")
    public Object aroundMethod(ProceedingJoinPoint pjp,
                               GroupMeetingLockInterface request) throws Throwable {
        groupMeetingLockService.lock(request.getGroupMeetingId());

        try {
            return pjp.proceed();
        } finally {
            groupMeetingLockService.unlock(request.getGroupMeetingId());
        }
    }
}
