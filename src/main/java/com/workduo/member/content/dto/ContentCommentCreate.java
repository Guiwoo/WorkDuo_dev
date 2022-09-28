package com.workduo.member.content.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ContentCommentCreate {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        private String comment;
    }
}
