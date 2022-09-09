package com.workduo.member.member.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class MemberLoginDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "이메일 은 필수 입력 사항 입니다.")
        private String email;
        @NotNull(message = "비밀번호 는 필수 입력 사항 입니다.")
        private String password;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String success;
        private Map<String,String> result;
    }
}
