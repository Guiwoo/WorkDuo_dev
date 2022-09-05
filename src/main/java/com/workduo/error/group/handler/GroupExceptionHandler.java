package com.workduo.error.group.handler;

import com.workduo.error.group.exception.GroupException;
import com.workduo.error.group.result.GroupErrorResult;
import com.workduo.error.group.type.GroupErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GroupExceptionHandler {

    @ExceptionHandler(GroupException.class)
    public ResponseEntity<GroupErrorResult> groupException(GroupException e) {
        GroupErrorCode groupErrorCode = e.getErrorCode();
        GroupErrorResult result = GroupErrorResult.of(e.getErrorCode());

        return new ResponseEntity<>(result, groupErrorCode.getHttpStatus());
    }
}
