package com.core.domain.group.dto;

import com.core.domain.group.entity.Group;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(example = "헬창 분들 모십니다. ?",description = "소개글 업데이트")
        private String introduce;

        @Min(value = 10, message = "그룹 인원은 최소 10명입니다.")
        @Max(value = 200, message = "그룹 인원은 최대 200명입니다.")
        @Schema(example = "30",description = "그룹 인원 을 수정 합니다.")
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

        public static Response fromEntity(Group group) {
            return Response.builder()
                    .limitPerson(group.getLimitPerson())
                    .introduce(group.getIntroduce())
                    .build();
        }
    }
}
