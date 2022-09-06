package com.workduo.error.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum GlobalExceptionType {

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"❌ 서버 내부 에러 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
