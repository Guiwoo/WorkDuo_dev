package com.workduo.member.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class MemberChangePassword {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name="MemberPassword")
    public static class Request {
        @NotNull(message = "비밀번호 는 필수 입력 사항 입니다.")
        @Schema(example = "1q2w3e4r@",description = "변경할 비빌번호 입니다.")
        private String password;
    }

}
