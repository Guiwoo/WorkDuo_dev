package com.workduo.group.group.dto;

import com.workduo.group.group.entity.Group;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UpdateGroup {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotNull(message = "그룹 소개글은 필수 입력 사항입니다.")
        private String introduce;

        @Min(value = 10, message = "그룹 인원은 최소 10명입니다.")
        @Max(value = 200, message = "그룹 인원은 최대 200명입니다.")
        private int limitPerson;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String introduce;
        private int limitPerson;

        public static UpdateGroup.Response fromEntity(Group group) {
            return Response.builder()
                    .limitPerson(group.getLimitPerson())
                    .introduce(group.getIntroduce())
                    .build();
        }
    }
}
