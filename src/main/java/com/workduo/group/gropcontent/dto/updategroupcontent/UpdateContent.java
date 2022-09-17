package com.workduo.group.gropcontent.dto.updategroupcontent;

import lombok.*;

import javax.validation.constraints.NotNull;

public class UpdateContent {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "제목은 필수 입력 사항입니다.")
        private String title;

        @NotNull(message = "내용은 필수 입력 사항입니다.")
        private String content;

        private boolean noticeYn;
        private int sortValue;
    }

}
