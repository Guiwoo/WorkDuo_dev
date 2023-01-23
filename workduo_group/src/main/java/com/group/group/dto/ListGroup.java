package com.group.group.dto;

import com.core.domain.group.dto.GroupDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;


public class ListGroup {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ListGroup")
    public static class Request {
        @Schema(example = "11140",description = "지역 아이디 순 으로 조회 합니다.")
        private String sgg;
        @Schema(example = "1",description = "스포츠 순 으로 조회 합니다.")
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
