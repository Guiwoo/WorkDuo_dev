package com.workduo.member.content.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

public class ContentCommentList {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private String success;
        private Page<MemberContentCommentDto> result;

        public static Response from(Page<MemberContentCommentDto> rs){
            return Response.builder()
                    .success("T")
                    .result(rs)
                    .build();
        }
    }
}
