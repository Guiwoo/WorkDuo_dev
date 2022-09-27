package com.workduo.group.groupmetting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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
    public static class Request {

        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        private String content;

        @NotNull(message = "모임 시작하는 날짜는 필수 입력 사항입니다.")
//        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/seoul")
        private LocalDateTime meetingStartDate;

        @NotNull(message = "모임 끝나는 날짜는 필수 입력 사항입니다.")
//        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/seoul")
        private LocalDateTime meetingEndDate;

        @NotBlank(message = "모임장소는필수입력사항입니다.")
        private String location;

        @Min(value = 1, message = "참여인원은 최소 1명입니다.")
        @Max(value = 100, message = "참여인원은 최대 100명입니다.")
        private int maxParticipant;
    }
}
