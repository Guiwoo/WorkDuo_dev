package com.workduo.group.groupmetting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateMeeting {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "CreateMeeting")
    public static class Request {

        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        @Schema(example = "Update Title YO",description = "제목 업데이트")
        private String title;

        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        @Schema(example = "HolyWak, Is there hell ?",description = "컨탠트 업데이트")
        private String content;

        @NotNull(message = "모임 시작하는 날짜는 필수 입력 사항입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/seoul")
        @Schema(example = "2022-10-05 18:00",description = "모임 시작시간 을 정합니다.")
        private LocalDateTime meetingStartDate;

        @NotNull(message = "모임 끝나는 날짜는 필수 입력 사항입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/seoul")
        @Schema(example = "2022-10-05 19:00",description = "모임 종료시간 을 정합니다.")
        private LocalDateTime meetingEndDate;

        @NotBlank(message = "모임장소는 필수 입력 사항입니다.")
        @Schema(example = "역삼역 골든짐",description = "모임 장소 를 정합니다.")
        private String location;

        @Min(value = 1, message = "참여인원은 최소 1명입니다.")
        @Max(value = 100, message = "참여인원은 최대 100명입니다.")
        @Schema(example = "30",description = "모임인원 을 정할수 있습니다. 1 ~ 100")
        private int maxParticipant;
    }
}
