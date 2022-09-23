package com.workduo.group.gropcontent.dto.updategroupcontentcomment;

import lombok.*;

import javax.validation.constraints.NotNull;

public class UpdateComment {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "내용은 필수 입력 사항입니다.")
        private String comment;
    }
}
