package com.workduo.member.content.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class ContentUpdate {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{
        @NotNull(message = "제목은 필수 입력 사항입니다.")
        private String title;

        @NotNull(message = "내용은 필수 입력 사항입니다.")
        private String content;

        private boolean noticeYn;
        @Min(value = 0, message = "정렬값은 최소 0 입니다.")
        private int sortValue;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String success;
        private Map<String,String> result;

        public static ContentCreate.Response from(){
            return ContentCreate.Response.builder()
                    .success("T")
                    .result(null)
                    .build();
        }
    }
}
