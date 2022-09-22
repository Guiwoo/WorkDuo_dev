package com.workduo.group.gropcontent.dto.detailgroupcontent;

import lombok.*;
import org.springframework.data.domain.Page;

public class DetailContentComment {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String success;
        private Page<GroupContentCommentDto> result;

        public static DetailContentComment.Response from(Page<GroupContentCommentDto> groupContentCommentDtos) {
            return DetailContentComment.Response.builder()
                    .success("T")
                    .result(groupContentCommentDtos)
                    .build();
        }
    }
}
