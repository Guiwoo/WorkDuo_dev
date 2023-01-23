package com.workduo.member.content.dto;

import com.core.domain.member.dto.MemberContentCommentDto;
import lombok.*;
import org.springframework.data.domain.Page;

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
