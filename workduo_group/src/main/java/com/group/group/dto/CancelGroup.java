package com.group.group.dto;

import lombok.*;

import java.util.Map;

public class CancelGroup {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String success;
        private Map<String, Object> result;

        public static CancelGroup.Response from() {
            return Response.builder()
                    .success("T")
                    .result(null)
                    .build();
        }
    }
}
