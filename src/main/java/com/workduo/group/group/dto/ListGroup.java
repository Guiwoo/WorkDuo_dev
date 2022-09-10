package com.workduo.group.group.dto;

import lombok.*;
import org.springframework.data.domain.Page;


public class ListGroup {

    @Getter
    @Setter
    public static class Request {
        private String sgg;
        private Integer sportId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String success;
        private Page<GroupDto> result;

        public static ListGroup.Response from(Page<GroupDto> groupDtos) {
            return ListGroup.Response.builder()
                    .success("T")
                    .result(groupDtos)
                    .build();
        }
    }

}
