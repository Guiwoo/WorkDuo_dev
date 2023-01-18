package com.workduo.group.groupmetting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class UpdateMeeting {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "UpdateMeeting")
    public static class Request {

        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        @Schema(example = "Update Title YO",description = "제목 업데이트")
        private String title;

        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        @Schema(example = "HolyWak, Is there hell ?",description = "컨탠트 업데이트")
        private String content;

        @NotBlank(message = "모임장소는필수입력사항입니다.")
        @Schema(example = "제주시 애월바다 서핑 센터",description = "장소 업데이트")
        private String location;

        @Min(value = 1, message = "참여인원은 최소 1명입니다.")
        @Max(value = 100, message = "참여인원은 최대 100명입니다.")
        @Schema(example = "50",description = "참여인원 을 정할수 있습니다. 1 ~ 100")
        private int maxParticipant;
    }
}
