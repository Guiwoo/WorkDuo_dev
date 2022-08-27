package com.workduo.member.member.dto.createmember;

import com.workduo.member.member.dto.MemberDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateMember {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        @NotNull
        private String email;

        @NotNull
        private String username;

        @NotNull
        private String password;

        @NotNull
        private String phoneNumber;

        @NotNull
        private String nickname;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String email;
        private String username;
        private LocalDateTime createdAt;

        public static CreateMember.Response from(MemberDto memberDto) {
            return CreateMember.Response.builder()
                    .email(memberDto.getEmail())
                    .username(memberDto.getUsername())
                    .createdAt(memberDto.getCreatedAt())
                    .build();
        }
    }
}
