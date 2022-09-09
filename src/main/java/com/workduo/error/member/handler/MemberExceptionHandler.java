package com.workduo.error.member.handler;

import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.result.MemberErrorResult;
import com.workduo.error.member.type.MemberErrorCode;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class MemberExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<MemberErrorResult> memberException(MemberException e) {
        MemberErrorCode MemberErrorCode = e.getErrorCode();
        MemberErrorResult result = MemberErrorResult.of(e.getErrorCode());

        return new ResponseEntity<>(result, MemberErrorCode.getHttpStatus());
    }
}

