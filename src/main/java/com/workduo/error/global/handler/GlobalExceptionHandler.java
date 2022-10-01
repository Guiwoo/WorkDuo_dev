package com.workduo.error.global.handler;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.workduo.error.global.result.GlobalErrorResult;
import com.workduo.error.global.result.ValidErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
@Order(3)
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            Exception.class,
            AmazonS3Exception.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<?> handleException(Exception e) {
        log.error("Exception is occurred.", e);
        if (e instanceof MethodArgumentNotValidException) {
            ValidErrorResult result = ValidErrorResult.of(
                    ((MethodArgumentNotValidException) e).getBindingResult()
            );
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        GlobalErrorResult result = GlobalErrorResult.builder()
                .success("F")
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(result, INTERNAL_SERVER_ERROR);
    }
}
