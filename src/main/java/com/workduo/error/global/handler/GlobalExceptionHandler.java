package com.workduo.error.global.handler;

import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.error.global.result.ValidErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("Exception is occurred.", e);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("❌ 서버 내부 에러 입니다.");
    }

    @ExceptionHandler(CustomMethodArgumentNotValidException.class)
    public ResponseEntity<ValidErrorResult> methodValidException(CustomMethodArgumentNotValidException e) {
        ValidErrorResult result = ValidErrorResult.of(e.getBindingResult());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
