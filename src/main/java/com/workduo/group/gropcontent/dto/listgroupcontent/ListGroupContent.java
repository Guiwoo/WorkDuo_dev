package com.workduo.group.gropcontent.dto.listgroupcontent;

import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentDto;
import lombok.*;
import org.springframework.data.domain.Page;

public class ListGroupContent {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String success;
        private Page<GroupContentDto> result;

        public static ListGroupContent.Response from(
                Page<GroupContentDto> groupContentDtos) {
            return Response.builder()
                    .success("T")
                    .result(groupContentDtos)
                    .build();
        }
    }
}
