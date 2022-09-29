package com.workduo.member.content.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

public class ContentCommentUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{

        @NotBlank
        private String comment;

    }

}
