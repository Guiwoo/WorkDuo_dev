package com.core.domain.memberContent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ContentMemberUpdate {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ContentUpdate")
    public static class Request{
        @NotNull(message = "제목은 필수 입력 사항입니다.")
        @Schema(example = "Update Title YO",description = "제목 업데이트")
        private String title;

        @NotNull(message = "내용은 필수 입력 사항입니다.")
        @Schema(example = "HolyWak, Is there hell ?",description = "컨탠트 업데이트")
        private String content;

        @Schema(example = "false",description = "공지사항")
        private boolean noticeYn;

        @Min(value = 0, message = "정렬값은 최소 0 입니다.")
        @Schema(example = "0",description = "정렬값 을 줄수 있습니다.")
        private int sortValue;
    }
}
