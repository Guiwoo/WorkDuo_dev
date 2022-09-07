package com.workduo.error.global.handler;

import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.error.global.result.GlobalErrorResult;
import com.workduo.error.global.result.ValidErrorResult;
import com.workduo.error.group.exception.GroupException;
import com.workduo.error.group.result.GroupErrorResult;
import com.workduo.error.group.type.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
@Order(3)
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomMethodArgumentNotValidException.class)
    public ResponseEntity<ValidErrorResult> methodValidException(CustomMethodArgumentNotValidException e) {
        ValidErrorResult result = ValidErrorResult.of(e.getBindingResult());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResult> handleException(Exception e) {
        log.error("Exception is occurred.", e);
        GlobalErrorResult result = GlobalErrorResult.builder()
                .success("F")
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(result, INTERNAL_SERVER_ERROR);
    }
}
