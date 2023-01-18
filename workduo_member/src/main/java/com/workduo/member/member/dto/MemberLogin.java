package com.workduo.member.member.dto;

import com.workduo.group.group.dto.CreateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class MemberLogin {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "MemberLogin")
    public static class Request {
        @NotNull(message = "이메일 은 필수 입력 사항 입니다.")
        @Schema(example = "rbsks147@hotmail.com",description = "이메일")
        private String email;
        @NotNull(message = "비밀번호 는 필수 입력 사항 입니다.")
        @Schema(example = "1a2s3d4f@",description = "비밀번호")
        private String password;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String success;
        private Map<String,String> result;
    }
}
