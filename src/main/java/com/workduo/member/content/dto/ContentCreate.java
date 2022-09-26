package com.workduo.member.content.dto;

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
    public static class Request {
        @NotNull(message = "제목 은 필수 입력 사항 입니다.")
        private String title;
        @NotNull(message = "내용 은 필수 입력 사항 입니다.")
        private String content;

        private boolean noticeYn; // 공지사항
        @Min(value = 0, message = "정렬값은 최소 0 입니다.")
        private int sortValue;

    }
}

