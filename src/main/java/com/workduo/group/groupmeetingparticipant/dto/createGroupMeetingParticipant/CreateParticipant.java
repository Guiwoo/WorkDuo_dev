package com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant;


import com.workduo.configuration.aop.groupmeeting.GroupMeetingLockInterface;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateParticipant {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request implements GroupMeetingLockInterface {
        @NotNull
        private Long groupMeetingId;

        @NotNull
        private Long memberId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long memberId;
        private Long groupMeetingId;
        private LocalDateTime meetingDate;

        public static Response from(CreateParticipantDto createParticipantDto) {
            return Response.builder()
                    .memberId(createParticipantDto.getMemberId())
                    .groupMeetingId(createParticipantDto.getGroupMeetingId())
                    .meetingDate(createParticipantDto.getMeetingDate())
                    .build();
        }
    }
}
