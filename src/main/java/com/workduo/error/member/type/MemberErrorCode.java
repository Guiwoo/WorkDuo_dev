package com.workduo.error.member.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MemberErrorCode {
    //일반 에러
    MEMBER_ERROR_NEED_LOGIN(HttpStatus.FORBIDDEN,"❌ 로그인 이 필요 합니다.(사용자 이메일이 다릅니다.)"),
    MEMBER_EMAIL_ERROR(HttpStatus.FORBIDDEN,"❌ 이메일 계정 이 존재하지 않습니다."),
    MEMBER_EMAIL_FORM(HttpStatus.FORBIDDEN,"❌ 이메일 형태가 아닙니다."),
    MEMBER_PASSWORD_ERROR(HttpStatus.FORBIDDEN,"❌ 패스워드 가 일치하지 않습니다."),
    MEMBER_PASSWORD_POLICY(HttpStatus.FORBIDDEN,"❌ 최소 8 최대20, 최소 문자 1개,숫자1개,특수문자 1개를 포함하고 있어야 합니다."),
    // 중복체크 에러
    MEMBER_EMAIL_DUPLICATE(HttpStatus.FORBIDDEN,"❌ 이미 존재하는 이메일 입니다."),
    MEMBER_NICKNAME_DUPLICATE(HttpStatus.FORBIDDEN,"❌ 이미 존재하는 닉네임 입니다."),
    MEMBER_PHONE_DUPLICATE(HttpStatus.FORBIDDEN,"❌ 이미 존재하는 전화번호 입니다."),
    MEMBER_PASSWORD_DUPLICATE(HttpStatus.FORBIDDEN,"❌ 이전 비밀번호 와 동일합니다."),
    // 삭제예정
    MEMBER_SIGG_ERROR(HttpStatus.FORBIDDEN,"❌ 삭제예정."),
    MEMBER_SPORT_ERROR(HttpStatus.FORBIDDEN,"❌ 삭제예정."),
    // 회원 상태 에러
    MEMBER_STOP_ERROR(HttpStatus.FORBIDDEN,"❌ 정지된 회원 입니다."),
    MEMBER_WITHDRAW_ERROR(HttpStatus.FORBIDDEN,"❌ 탈퇴한 회원 입니다."),
    MEMBER_REFRESH_TOKEN_ERROR(HttpStatus.FORBIDDEN,"❌ 탈퇴한 회원 입니다."),
    // 멤버 피드 에러
    MEMBER_CONTENT_UPDATE_AUTHORIZATION(HttpStatus.FORBIDDEN,"❌ 수정 권한 이 없습니다."),
    MEMBER_CONTENT_DOES_NOT_EXIST(HttpStatus.BAD_REQUEST,"❌ 게시글 이 존재 하지 않습니다."),
    MEMBER_CONTENT_DELETED(HttpStatus.BAD_REQUEST,"❌ 삭제된 게시글 입니다.");



    private final HttpStatus httpStatus;
    private final String message;
}
