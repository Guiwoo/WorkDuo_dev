package com.workduo.configuration.aop.groupmeeting;

import com.workduo.error.group.exception.GroupException;
import com.workduo.error.group.type.GroupErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import static com.workduo.error.group.type.GroupErrorCode.GROUP_MEETING_LOCK_FAIL;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class GroupMeetingLockAopAspect {

    private final GroupMeetingLockService groupMeetingLockService;

    @Around("@annotation(com.workduo.configuration.aop.groupmeeting.GroupMeetingLock) && args(groupId, meetingId)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp, Long groupId, Long meetingId) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        GroupMeetingLock groupMeetingLock = methodSignature.getMethod().getAnnotation(GroupMeetingLock.class);

        try {
            groupMeetingLockService.lock(meetingId, groupMeetingLock.tryLockTime());
            return pjp.proceed();
        } catch (InterruptedException e) {
            throw new GroupException(GROUP_MEETING_LOCK_FAIL);
        } finally {
            groupMeetingLockService.unlock(meetingId);
        }
    }
}
