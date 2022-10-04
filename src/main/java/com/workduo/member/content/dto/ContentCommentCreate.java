package com.workduo.member.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ContentCommentCreate {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "ContentCommentCreate")
    public static class Request{
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        @Schema(example = "여기가 천국인가요 호올리",description = "코멘트 내용 기입")
        private String comment;
    }
}
