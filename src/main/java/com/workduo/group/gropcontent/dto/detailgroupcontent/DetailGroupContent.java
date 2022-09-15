package com.workduo.group.gropcontent.dto.detailgroupcontent;

import lombok.*;

public class DetailGroupContent {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String success;
        private DetailGroupContentDto result;

        public static DetailGroupContent.Response from(DetailGroupContentDto detailGroupContentDto) {
            return Response.builder()
                    .success("T")
                    .result(detailGroupContentDto)
                    .build();
        }
    }
}
