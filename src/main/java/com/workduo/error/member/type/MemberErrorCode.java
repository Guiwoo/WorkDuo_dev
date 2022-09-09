package com.workduo.error.member.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MemberErrorCode {

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"❌ 서버 내부 에러 입니다."),
    MEMBER_EMAIL_ERROR(HttpStatus.FORBIDDEN,"❌ 이메일 계정 이 존재하지 않습니다."),
    MEMBER_PASSWORD_ERROR(HttpStatus.FORBIDDEN,"❌ 패스워드 가 일치하지 않습니다."),
    MEMBER_STOP_ERROR(HttpStatus.FORBIDDEN,"❌ 정지된 회원 입니다."),
    MEMBER_WITHDRAW_ERROR(HttpStatus.FORBIDDEN,"❌ 탈퇴한 회원 입니다."),

    MEMBER_REFRESH_TOKEN_ERROR(HttpStatus.FORBIDDEN,"❌ 탈퇴한 회원 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
