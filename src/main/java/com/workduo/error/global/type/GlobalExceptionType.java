package com.workduo.error.global.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workduo.error.global.result.GlobalErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum GlobalExceptionType {

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"❌ 서버 내부 에러 입니다."),
    LOGIN_ERROR(HttpStatus.UNAUTHORIZED,"❌ 로그인 이 필요 합니다"),
    AUTHORIZATION_ERROR(HttpStatus.FORBIDDEN,"❌ 권한 이 없습니다");

    private final HttpStatus httpStatus;
    private final String message;

    public static String responseJsonString(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(GlobalErrorResult.builder()
                .success("F")
                .message(message)
                .result(null)
                .build());
    }
}
