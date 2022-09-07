package com.workduo.group.group.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class CreateGroup {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "그룹이름은 필수 입력 사항입니다.")
        private String name;

        @Min(value = 10, message = "그룹 인원은 최소 10명입니다.")
        @Max(value = 200, message = "그룹 인원은 최대 200명입니다.")
        private int limitPerson;

        @Min(value = 1, message = "운동은 필수 선택 사항입니다.")
        private int sportId;

        @Min(value = 1, message = "지역은 필수 선택 사항입니다.")
        private int siggAreaId;

        @NotNull(message = "그룹 소개글은 필수 입력 사항입니다.")
        private String introduce;

        @NotNull(message = "그룹 썸네일은 필수 입력 사항입니다.")
        private String thumbnailPath;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String success;
        private Map<String, Object> result;

        public static CreateGroup.Response from() {
            return Response.builder()
                    .success("T")
                    .result(null)
                    .build();
        }
    }

}
