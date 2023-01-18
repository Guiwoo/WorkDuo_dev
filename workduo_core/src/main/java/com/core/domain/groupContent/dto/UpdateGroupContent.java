package com.core.domain.groupContent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

public class UpdateGroupContent {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "UpdateContent")
    public static class Request {
        @NotNull(message = "제목은 필수 입력 사항입니다.")
        @Schema(example = "Update Title YO",description = "제목 업데이트")
        private String title;

        @NotNull(message = "내용은 필수 입력 사항입니다.")
        @Schema(example = "HolyWak, Is there hell ?",description = "컨탠트 업데이트")
        private String content;

        @Schema(example = "false",description = "공지사항")
        private boolean noticeYn;

        @Schema(example = "0",description = "정렬값 을 줄수 있습니다.")
        private int sortValue;
    }

}
