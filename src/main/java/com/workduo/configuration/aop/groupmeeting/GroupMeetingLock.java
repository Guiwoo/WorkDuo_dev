package com.workduo.configuration.aop.groupmeeting;

import javax.persistence.Inheritance;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inheritance
public @interface GroupMeetingLock {
    // default값 시간동안 lock이 해지될 때 까지 기다림
    long tryLockTime() default 5000L;
}
