package com.core.config.aop.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

    @Around("execution(* com.*..*Controller.*(..))")
    public Object methodLogger(ProceedingJoinPoint pjp) throws Throwable {

        try {
            Object result = pjp.proceed();

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            String controllerName = pjp.getSignature().getDeclaringType().getSimpleName();
            String methodName = pjp.getSignature().getName();

            Map<String, Object> params = new HashMap<>();

            try {
                params.put("controller", controllerName);
                params.put("method", methodName);
                params.put("log_time", LocalDateTime.now());
                params.put("request_uri", request.getRequestURI());
                params.put("http_method", request.getMethod());
            } catch (Exception e) {
                log.error("LoggerAspect error", e);
            }

            log.info("params : {}", params);

            return result;
        } catch (Throwable throwable) {
            throw throwable;
        }
    }
}
