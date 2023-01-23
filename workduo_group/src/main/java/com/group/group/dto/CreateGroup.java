package com.group.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(name = "CreateGroup")
    public static class Request {
        @NotNull(message = "그룹이름은 필수 입력 사항입니다.")
        @Schema(example = "Yoga 하실 남성 분 구합니다.",description = "그룹 이름")
        private String name;

        @Min(value = 10, message = "그룹 인원은 최소 10명입니다.")
        @Max(value = 200, message = "그룹 인원은 최대 200명입니다.")
        @Schema(example = "50",description = "그룹의 인원을 정할수 있습니다. 10 ~ 200")
        private int limitPerson;

        @Min(value = 1, message = "운동은 필수 선택 사항입니다.")
        @Schema(example = "1",description = "운동 의 아이디 를 파라미터로 보냅니다.")
        private int sportId;

        @NotNull(message = "지역은 필수 선택 사항입니다.")
        @Schema(example = "11140",description = "지역 의 아이디 를 파라미터로 보냅니다.")
        private String sgg;

        @NotNull(message = "그룹 소개글은 필수 입력 사항입니다.")
        @Schema(example = "Ouu 나 핫 보이",description = "그룹 의 간략한 소개글을 작성합니다.")
        private String introduce;

//        @NotNull(message = "그룹 썸네일은 필수 입력 사항입니다.")
//        private String thumbnailPath;

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
