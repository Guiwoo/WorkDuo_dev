package com.workduo.member.content.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class ContentCreate {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ContentCreate")
    public static class Request {
        @NotNull(message = "제목 은 필수 입력 사항 입니다.")
        @Schema(example = "오늘 등 잘먹었다. 크",description = "피드 타이틀 작성")
        private String title;

        @NotNull(message = "내용 은 필수 입력 사항 입니다.")
        @Schema(example = "데드,로우,풀다운 슈퍼세트",description = "피드 본문 작성")
        private String content;

        @Schema(example = "false",description = "공지사항")
        private boolean noticeYn; // 공지사항

        @Min(value = 0, message = "정렬값은 최소 0 입니다.")
        @Schema(example = "0",description = "정렬값 을 줄수 있습니다.")
        private int sortValue;

    }
}

